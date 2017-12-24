package com.apm29.kotlinapp.ui

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import android.view.Window
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.logD
import com.apm29.network.ApiCall
import com.apm29.network.api.OneAPi
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 带Toolbar和Menu
 */
open class BaseMaterialActivity : BaseActivity<BaseMaterialActivity.MaterialDefaultPresenter>() {
    override var drawStatusBar: Boolean = true

    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout() = R.layout.activity_base_material

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<Toolbar>(R.id.tool_bar).also {
            setSupportActionBar(it)
        }
    }

    override fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
        (mPresenter ).getDailyIdList().also { mDisposables.add(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        window.addFlags(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun getPresenter(): MaterialDefaultPresenter = MaterialDefaultPresenter(this)
    open class MaterialDefaultPresenter(ui: BaseUI) : BasePresenter(ui) {
        fun getDailyIdList(): Disposable {
            return ApiCall.oneApi(ui as Context).create(OneAPi::class.java)
                    .getContent(uuid = "ffffffff-a90e-706a-63f7-ccf973aae5ee")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                logD(it.toString())
                                ui.onNewData(it)
                            },
                            {
                                logD(it.toString())
                                ui.onError(it.message)
                            },
                            {
                                ui.stopLoading()
                            },
                            {
                                ui.startLoading()
                            }
                    )
        }
    }
}