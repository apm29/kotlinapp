package com.apm29.kotlinapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.apm29.beanmodule.beans.one.ContentListItem
import com.apm29.beanmodule.beans.one.DataList
import com.apm29.beanmodule.beans.one.OneList
import com.apm29.beanmodule.beans.one.Weather
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.logD
import com.apm29.kotlinapp.utils.showToast
import com.apm29.kotlinapp.utils.uuid
import com.apm29.network.ApiCall
import com.apm29.network.api.OneAPI
import com.bumptech.glide.Glide
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_base_material.*

/**
 * 带Toolbar和Menu
 */
open class SampleMaterialActivity : BaseActivity<SampleMaterialActivity.MaterialDefaultPresenter>() {
    override var drawStatusBar: Boolean = true
    var page = 1
    lateinit var dataList: DataList
    val mRecyclerView: RecyclerView by lazy {
        return@lazy findViewById<RecyclerView>(R.id.recycler_view)
    }

    override fun enableLoadMore(): Boolean {
        return true
    }

    override fun onNewData(data: Any?) {
        if (data != null && data is DataList && data.res == 0) {
            dataList = data
            mPresenter.getContent(data.data?.get(page - 1) ?: "")
        } else if (data != null && data is OneList && data.res == 0) {
            setupOneList(data)
        }
    }

    private fun setupOneList(oneList: OneList) {
        setupHeader(oneList.data?.weather)
        //mRecyclerView.layoutManager.isAutoMeasureEnabled=true
        //mRecyclerView.setHasFixedSize(false)
        val contentList = oneList.data?.contentList ?: ArrayList()
        if (page == 1)
            mRecyclerView.adapter = MyAdapter(contentList)
        else{
            (mRecyclerView.adapter as MyAdapter).data.addAll(contentList)
            //mRecyclerView.adapter.notifyDataSetChanged()
        }
        mRecyclerView.requestLayout()
        page += 1
        baseRefreshLayout.setOnLoadmoreListener {
            if (page < dataList.data?.size ?: 0)
                mPresenter.getContent(dataList.data?.get(page - 1) ?: "2000").also { mDisposables.add(it) }
            else {
                it.finishLoadmore(300)
                showToast("没有更多数据啦")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader(weather: Weather?) {
        tv_date.text = weather?.date
        tv_weather.text = weather?.cityName + " - " + weather?.climate + " - " + weather?.humidity + " - " + weather?.hurricane


    }

    override fun getDefaultLayout() = R.layout.activity_base_material

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<Toolbar>(R.id.tool_bar).also {
            setSupportActionBar(it)
        }
//        findViewById<ImageView>(R.id.btn_snack).setOnClickListener {
//            Snackbar.make(it, "click!", Snackbar.LENGTH_SHORT).show()
//        }
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mPresenter.getDailyIdList().also { mDisposables.add(it) }
    }

    override fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
        page = 1
        (mPresenter).getDailyIdList().also { mDisposables.add(it) }
    }

    override fun getPresenter(): MaterialDefaultPresenter = MaterialDefaultPresenter(this)
    open class MaterialDefaultPresenter(ui: BaseUI) : BasePresenter(ui) {
        fun getDailyIdList(): Disposable {
            ui.startLoading()
            return ApiCall.oneApi(ui as Context).create(OneAPI::class.java)
                    .getIdList(uuid = uuid(ui as Context))
                    .firstOrError()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                logD(it.toString())
                                ui.onNewData(it)
                                ui.stopLoading()
                            },
                            {
                                logD(it.toString())
                                ui.onError(it.message)
                                ui.stopLoading()
                            }
                    )
        }

        fun getContent(code: String): Disposable {
            ui.startLoading()
            return ApiCall.oneApi(ui as Context).create(OneAPI::class.java)
                    .getContent(code)
                    .firstOrError()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                logD(it.toString())
                                ui.onNewData(it)
                                ui.stopLoading()
                            },
                            {
                                logD(it.toString())
                                ui.onError(it.message)
                                ui.stopLoading()
                            }
                    )
        }
    }

    class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    class MyAdapter(var data: ArrayList<ContentListItem> = ArrayList()) : RecyclerView.Adapter<Holder>() {
        override fun onBindViewHolder(holder: Holder?, position: Int) {

            val textView = holder?.itemView?.findViewById<TextView>(R.id.tv)
            textView?.text = data[position].forward

            Glide.with(textView?.context).load(data[position].imgUrl)
                    .fitCenter().into(holder?.itemView?.findViewById<ImageView>(R.id.iv))


        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
            val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.item_one_list, parent, false)
            return Holder(inflate)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }
}