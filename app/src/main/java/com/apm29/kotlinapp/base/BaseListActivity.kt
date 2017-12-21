package com.apm29.kotlinapp.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.apm29.kotlinapp.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BaseListActivity<ITEM_TYPE, P : BaseListActivity.ListPresenter> : BaseActivity<P>() {

    protected val startIndex = 1
    protected var pageSize = 10
    protected var page: Int = startIndex
    protected var mAdapter: BaseAdapter? = null
    protected var mData: List<ITEM_TYPE>? = ArrayList()
    protected var mDisposables: CompositeDisposable = CompositeDisposable()

    /** UI References*/
    private lateinit var mRecyclerView: RecyclerView

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

    override fun  onNewData(data: Any?) {
        if (data is List<*>) {
            try {
                setListData(data = data as List<ITEM_TYPE>)
            } catch (e: Exception) {
                onError("data cast failed, data type doesn't match" + e.message)
            }
        } else if (data == null) {
            onEmpty()
        }
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        setUpList()
        baseRefreshLayout.autoRefresh(300)
    }

    final override fun getDefaultLayout(): Int {
        return R.layout.activity_base_list_layout
    }

    protected fun setUpList() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = BaseAdapter(getResID(), data = mData)
        mRecyclerView.adapter = mAdapter
        baseRefreshLayout.setOnRefreshListener {
            page = 1
            mPresenter.loadData().also { mDisposables.add(it) }
        }
        baseRefreshLayout.setOnLoadmoreListener {
            mPresenter.loadData().also { mDisposables.add(it) }
        }
        findViewById<TextView>(R.id.tv_base_empty).setOnClickListener {
            mPresenter.loadData().also { mDisposables.add(it) }
        }
    }


    protected fun setListData(data: List<ITEM_TYPE>) {

        if (mAdapter == null) {
            mAdapter = BaseAdapter(getResID(),data =data, emptyView = TextView(this))
            mRecyclerView.adapter = mAdapter
        }
        if (data.isEmpty()) {
            onEmpty()
        }
        page++
        if (page == startIndex)
            mAdapter?.setNewData(data)
        else{
            mAdapter?.addData(data)
        }
        completeRefresh()
    }

    /**绑定item数据*/
    abstract fun bindItemView(holder: BaseVH, position: Int, itemViewType: Int, item: ITEM_TYPE?)

    /**创建Holder*/
    abstract fun BaseAdapter.getHolder(parent: ViewGroup?, viewType: Int, inflate: View): BaseVH

    /**item布局*/
    protected abstract fun getResID(): Int

    override fun onDestroy() {
        super.onDestroy()
        if (!mDisposables.isDisposed)
            mDisposables.dispose()
    }


    abstract class ListPresenter(ui: BaseUI) : BasePresenter(ui) {
        abstract fun loadData(): Disposable
    }

    inner open class BaseAdapter(@LayoutRes val itemLayoutRes: Int,
                                 val data: List<ITEM_TYPE>?= ArrayList(),
                                 var emptyView: TextView? = null, var emptyDesc: String = "暂无数据") : RecyclerView.Adapter<BaseVH>() {
        init {
            if(data==null)
                throw IllegalArgumentException("data 不可为null")
        }
        override fun onBindViewHolder(holder: BaseVH, position: Int) {
            if (getItemViewType(position) == ITEM_VIEW_TYPE_EMPTY && holder is DefaultVH) {
                (holder.itemView as TextView).text = emptyDesc
            }
            bindItemView(holder, position, getItemViewType(position), data?.get(position))
        }

        override fun getItemCount(): Int {
            return data?.size ?: 1
        }

        override fun getItemViewType(position: Int): Int {
            if (showEmpty() && position == 0) return ITEM_VIEW_TYPE_EMPTY
            return super.getItemViewType(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseVH {
            if (viewType == ITEM_VIEW_TYPE_EMPTY) {
                if (emptyView == null) {
                    emptyView = TextView(parent?.context)
                    (emptyView as TextView).text = emptyDesc
                }
                return DefaultVH(emptyView)
            }
            return getHolder(parent, viewType, LayoutInflater.from(parent?.context).inflate(itemLayoutRes, parent, false))
        }

        /**设置新数据*/
        fun setNewData(data: List<ITEM_TYPE>) {
            if (this.data!=null&&this.data is ArrayList){
                this.data.clear()
                this.data.addAll(data)
            }
            notifyDataSetChanged()
        }
        /**添加新数据*/
        fun addData(data: List<ITEM_TYPE>) {
            if (this.data!=null&&this.data is ArrayList){
                this.data.addAll(data)
            }
            notifyDataSetChanged()
        }
        /**是否展示空视图*/
        fun showEmpty(): Boolean {
            return data == null || data?.size == 0
        }


    }

    open class BaseVH(itemView: View?) : RecyclerView.ViewHolder(itemView)
    open class DefaultVH(itemView: View?) : BaseVH(itemView)
}

const val ITEM_VIEW_TYPE_EMPTY: Int = 999



