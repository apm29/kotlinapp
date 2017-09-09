package com.apm29.kotlinapp.base

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

abstract class BaseListActivity<DATA_TYPE,P:ListPresenter> : BaseActivity<P>() {
    /**
     * UI References
     */
    private val mRecyclerView:RecyclerView by lazy {
        return@lazy findViewById(R.id.recycler_view) as RecyclerView
    }

    override fun onError(error: String?) {
        val textView = TextView(this)
        textView.setText(error)
        mAdapter?.emptyView= textView
    }

    override fun <N : Any?> onNewData(data: N) {
        if (data is List<*>)
            try {
                setListData(data = data as MutableList<DATA_TYPE>)
            }catch (e:Exception){
                onError("data cast failed, data type doesn't match")
            }
    }

    var mAdapter:BaseAdapter?=null
    var mData:List<DATA_TYPE>?=null
    var mDisLoadData:Disposable?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_list_layout)
        setUpList()
        mDisLoadData = mPresenter.loadData()
    }
    protected fun setUpList() {
        mRecyclerView.layoutManager=LinearLayoutManager(this)
        mAdapter = BaseAdapter(getResID(), mData)
        mRecyclerView.adapter = mAdapter
        mAdapter?.isUpFetchEnable=true
        mAdapter?.isUpFetching=false
        mAdapter?.setUpFetchListener {
            mAdapter?.isUpFetching = true
            mPresenter.loadData()
        }
    }
    protected fun setListData(data: MutableList<DATA_TYPE>) {
        if (mAdapter==null) {
            mAdapter = BaseAdapter(getResID(), data = data)
            mRecyclerView.adapter = mAdapter
        }
        mAdapter?.setNewData(data)
    }

    protected abstract fun getResID(): Int

    override fun onDestroy() {
        super.onDestroy()
        if (mDisLoadData !=null&&!mDisLoadData!!.isDisposed)
            mDisLoadData?.dispose()
    }
    abstract fun convertView(helper: BaseHolder?, item: DATA_TYPE)

    inner class BaseAdapter(@LayoutRes resId:Int, data: List<DATA_TYPE>?) : BaseQuickAdapter<DATA_TYPE, BaseHolder>(resId,data) {

        override fun convert(helper: BaseHolder?, item: DATA_TYPE) {
            convertView(helper,item)
        }

        // constructor( data: List<DATA_TYPE>?) : this(0,data) {
        //
        // }
    }
}
abstract class ListPresenter(ui:BaseUI?): BasePresenter(ui){
    abstract fun loadData(): Disposable
}
class BaseHolder(view: View?) : BaseViewHolder(view) {
}

