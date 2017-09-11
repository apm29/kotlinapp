package com.apm29.kotlinapp.base

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrDefaultHandler2
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler2
import `in`.srain.cube.views.ptr.header.StoreHouseHeader
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.view.ptr.PtrCustomLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.disposables.Disposable



abstract class BaseListActivity<DATA_TYPE, P : ListPresenter> : BaseActivity<P>() {

    var page:Int=1;
    /**
     * UI References
     */
    private val mRecyclerView: RecyclerView by lazy {
        return@lazy findViewById(R.id.recycler_view) as RecyclerView
    }
    private val mRefreshLayout: PtrCustomLayout by lazy {
        return@lazy findViewById(R.id.refresh_layout) as PtrCustomLayout
    }

    override fun onError(error: String?) {
        val textView = TextView(this)
        textView.text = error
        mAdapter?.emptyView = textView
        mRefreshLayout.refreshComplete()
    }

    override fun <N : Any?> onNewData(data: N) {
        if (data is List<*>)
            try {
                setListData(data = data as MutableList<DATA_TYPE>)
            } catch (e: Exception) {
                onError("data cast failed, data type doesn't match")
            }
    }

    var mAdapter: BaseAdapter? = null
    var mData: List<DATA_TYPE>? = null
    var mDisLoadData: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_list_layout)
        setUpList()
        mDisLoadData = mPresenter.loadData()
    }

    protected fun setUpList() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = BaseAdapter(getResID(), mData)
        mRecyclerView.adapter = mAdapter
        mAdapter?.isUpFetchEnable = true
        mAdapter?.isUpFetching = false
        mAdapter?.setUpFetchListener {
            mAdapter?.isUpFetching = true
            mPresenter.loadData()
        }


        val footer = StoreHouseHeader(this)
        footer.setTextColor(R.color.colorAccent)
        footer.initWithStringArray(R.array.storehouse)
        mRefreshLayout.setFooterView(footer)
        mRefreshLayout.addPtrUIHandler(footer)
        mRefreshLayout.setPtrHandler(object : PtrHandler2{
            override fun onLoadMoreBegin(frame: PtrFrameLayout?) {
                page+=1
                mPresenter.loadData()
            }

            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                page=1
                mPresenter.loadData()
            }


            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header)
            }

            override fun checkCanDoLoadMore(frame: PtrFrameLayout?, content: View?, footer: View?): Boolean {
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame,content,footer)
            }

        })
    }


    protected fun setListData(data: MutableList<DATA_TYPE>) {
        if (mAdapter == null) {
            mAdapter = BaseAdapter(getResID(), data = data)
            mRecyclerView.adapter = mAdapter
        }
        mAdapter?.setNewData(data)
        mRefreshLayout.refreshComplete()
    }

    protected abstract fun getResID(): Int

    override fun onDestroy() {
        super.onDestroy()
        if (mDisLoadData != null && !mDisLoadData!!.isDisposed)
            mDisLoadData?.dispose()
    }

    abstract fun convertView(helper: BaseHolder?, item: DATA_TYPE)

    inner class BaseAdapter(@LayoutRes resId: Int, data: List<DATA_TYPE>?) : BaseQuickAdapter<DATA_TYPE, BaseHolder>(resId, data) {

        override fun convert(helper: BaseHolder?, item: DATA_TYPE) {
            convertView(helper, item)
        }
    }
}

abstract class ListPresenter(ui: BaseUI?) : BasePresenter(ui) {
    abstract fun loadData(): Disposable
}

class BaseHolder(view: View?) : BaseViewHolder(view) {
}


