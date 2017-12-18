package com.apm29.kotlinapp.base

import `in`.srain.cube.views.ptr.header.StoreHouseHeader
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.apm29.kotlinapp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.disposables.Disposable



abstract class BaseListActivity<DATA_TYPE, P : BaseListActivity.ListPresenter> : BaseActivity<P>() {

    var page:Int=1
    /**
     * UI References
     */
    private val mRecyclerView: RecyclerView by lazy {
        return@lazy findViewById<RecyclerView>(R.id.recycler_view)
    }

    override fun onError(error: String?) {
        val textView = TextView(this)
        textView.text = error
        mAdapter?.emptyView = textView
        completeRefresh()
    }
    protected fun completeRefresh() {
        baseRefreshLayout.finishRefresh(300)
        baseRefreshLayout.finishLoadmore(300)
    }

    override fun <N : Any?> onNewData(data: N) {
        if (data is MutableList<*>) {
            try {
                setListData(data = data as MutableList<DATA_TYPE>)
            } catch (e: Exception) {
                onError("data cast failed, data type doesn't match")
            }
        } else if (data==null ){
            onEmpty()
        }
    }

    protected var mAdapter: BaseAdapter? = null
    protected var mData: List<DATA_TYPE>? = null
    protected var mDisLoadData: Disposable? = null

    override fun setupViews(savedInstanceState: Bundle?) {
        setUpList()
        mDisLoadData = mPresenter.loadData()
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_base_list_layout
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
        baseRefreshLayout.setOnRefreshListener {
            page=1
            mPresenter.loadData()
        }
        baseRefreshLayout.setOnLoadmoreListener {
            page++
            mPresenter.loadData()
        }
        findViewById<TextView>(R.id.tv_base_empty).setOnClickListener {
            mPresenter.loadData()
        }
    }


    protected fun setListData(data: MutableList<DATA_TYPE>) {
        if (mAdapter == null) {
            mAdapter = BaseAdapter(getResID(), data = data)
            mRecyclerView.adapter = mAdapter
        }
        if(data.size==0){
            onEmpty()
        }
        mAdapter?.setNewData(data)
        completeRefresh()
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


    abstract class ListPresenter(ui: BaseUI) : BasePresenter(ui) {
        abstract fun loadData(): Disposable
    }

    open class BaseHolder(view: View?) : BaseViewHolder(view)
}




