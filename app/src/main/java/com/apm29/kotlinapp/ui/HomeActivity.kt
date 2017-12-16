package com.apm29.kotlinapp.ui

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.apm29.beanmodule.Init.HomeViewData
import com.apm29.guideview.Focus
import com.apm29.guideview.NightVeil
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.ui.account.LoginActivity
import com.apm29.kotlinapp.ui.subscription.SubscriptionManagerActivity
import com.apm29.network.ApiCall
import com.apm29.network.api.API
import com.apm29.network.cache.AccountCache
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home_layout.*


class HomeActivity : BaseActivity<HomeActivity.HomePresenter>() {
    override fun <N : Any?> onNewData(data: N) {
        if (data is HomeViewData) {
            val tvHello = findViewById<TextView>(R.id.tv_hello)
            tvHello.text = data.toString()
        }
    }

    override fun onError(error: String?) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        val tvHello = findViewById<TextView>(R.id.tv_hello)
        tvHello.text = error
    }

    override fun getPresenter(): HomePresenter = HomePresenter(this)

    private var subscribe: Disposable? = null

    override fun getDefaultLayout(): Int {
        return R.layout.activity_home_layout
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        subscribe = mPresenter.loadNetData()

        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            if (AccountCache.getUserInfo(this) == null)
                LoginActivity.starter(this)
            else {
                Toast.makeText(this, "已经登录", Toast.LENGTH_SHORT).show()
                PagerActivity.starter(this)
            }
        }
        val btnSubscribe = findViewById<Button>(R.id.btn_subscribe_mine)
        btnSubscribe.setOnClickListener {
            SubscriptionManagerActivity.starter(this)
        }
        showGuide(btnSubscribe, btnLogin)
        tv_hello.setOnClickListener {
            NightVeil.show("btnLogin",this)
        }
    }

    override fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
       mPresenter.loadNetData()
    }

    private fun showGuide(btnSubscribe: View?, btnLogin: View?) {
        //引导图

        val controller1 = NightVeil.from(this).setControllerTag("btnLogin").addFocus(Focus(btnLogin!!, null, Focus.TYPE.CIRCLE))

        NightVeil
                .from(this)
                .addFocus(Focus(btnSubscribe!!, object : Focus.HitFocusListener {
                    override fun onHit(focus: Focus): Boolean {
                        println("hit " + focus.view)
                        focus.view?.performClick()
                        focus.controller.remove()
                        controller1.show()
                        return true
                    }
                }))
                .addFocus(Focus(
                        RectF(300F,300F,600F,700F),
                        radius = 40F
                ))
                .setBackgroundColorRes(R.color.design_snackbar_background_color)
                .setCancelableAnyWhere(true)
                .setLayout(R.layout.activity_home_guide_layout)
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    class HomePresenter(ui: BaseUI) : BasePresenter(ui) {
        fun loadNetData(): Disposable {
            return ApiCall.mainService(ui as Context)
                    .create(API.Home::class.java)
                    .initHomeViewData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .map {
                        if (it.meta?.code != 200) {
                            ui.onError(it.meta?.desc)
                        }
                        ui.stopLoading()
                        println(it)
                        return@map it
                    }
                    .subscribe(
                            {
                                println("result:" + it)
                                ui.onNewData(it.data)
                            },
                            {
                                println("error:" + it)
                                ui.stopLoading()
                                ui.onError(it.message)
                            },
                            {
                                println("complete")
                                ui.stopLoading()
                            },
                            {
                                println("onSubScribed")
                                ui.startLoading()
                            }
                    )

        }

        fun fetchIndustry(): Disposable {
            return ApiCall.mainService((ui as Activity))
                    .create(API.Init::class.java)
                    .fetchIndustryCategory()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }
}

