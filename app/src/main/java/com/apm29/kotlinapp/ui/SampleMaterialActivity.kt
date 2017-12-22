package com.apm29.kotlinapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import android.view.Window
import com.apm29.beanmodule.beans.one.DataList
import com.apm29.beanmodule.beans.one.OneList
import com.apm29.beanmodule.beans.one.Weather
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.logD
import com.apm29.kotlinapp.utils.uuid
import com.apm29.network.ApiCall
import com.apm29.network.api.OneAPI
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_base_material.*

/**
 * 带Toolbar和Menu
 */
open class SampleMaterialActivity<P : BasePresenter> : BaseActivity<P>() {
    override var drawStatusBar: Boolean = true

    override fun onNewData(data: Any?) {
        if (data != null && data is DataList && data.res == 0) {
            (mPresenter as MaterialDefaultPresenter).getContent(data.data?.get(0) ?: "")
        } else if (data != null && data is OneList && data.res == 0) {
            setupOneList(data)
        }
    }

    private fun setupOneList(oneList: OneList) {
        setupHeader(oneList.data?.weather)
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader(weather: Weather?) {
        tv_date.text = weather?.date
        tv_weather.text=weather?.cityName+" - "+weather?.climate+" - " +weather?.humidity+" - "+weather?.hurricane
    }

    override fun getDefaultLayout() = R.layout.activity_base_material

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<Toolbar>(R.id.tool_bar).also {
            setSupportActionBar(it)
        }
    }

    override fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
        (mPresenter as MaterialDefaultPresenter).getDailyIdList().also { mDisposables.add(it) }
    }

    override fun getPresenter(): P = MaterialDefaultPresenter(this) as P
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

        fun getContent(code: String) {
            ui.startLoading()
            ApiCall.oneApi(ui as Context).create(OneAPI::class.java)
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
}