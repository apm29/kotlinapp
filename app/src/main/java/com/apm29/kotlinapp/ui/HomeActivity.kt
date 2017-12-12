package com.apm29.kotlinapp.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.apm29.beanmodule.Init.HomeViewData
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.ui.account.LoginActivity
import com.apm29.kotlinapp.ui.subscription.SubscriptionManagerActivity
import com.apm29.network.ApiCall
import com.apm29.network.api.API
import com.apm29.network.cache.AccountCache
import com.app.hubert.library.Controller
import com.app.hubert.library.HighLight
import com.app.hubert.library.NewbieGuide
import com.app.hubert.library.OnGuideChangedListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class HomeActivity : BaseActivity<HomePresenter>() {
    override fun <N : Any?> onNewData(data: N) {
        if (data is HomeViewData) {
            val tvHello = findViewById(R.id.tv_hello) as TextView
            tvHello.text = data.toString()
        }
    }

    override fun onError(error: String) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        val tvHello = findViewById(R.id.tv_hello) as TextView
        tvHello.text = error
    }

    override fun getPresenter(): HomePresenter = HomePresenter(this)

    private var subscribe: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_layout)
        subscribe = mPresenter.loadNetData()

        val btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            if (AccountCache.getUserInfo(this) == null)
                LoginActivity.starter(this)
            else {
                Toast.makeText(this, "已经登录", Toast.LENGTH_SHORT).show()
                PagerActivity.starter(this)
            }
        }
        val btnSubscribe = findViewById(R.id.btn_subscribe_mine)
        btnSubscribe.setOnClickListener {
            SubscriptionManagerActivity.starter(this)
        }
        showGuide(btnSubscribe, btnLogin)
    }

    private fun showGuide(btnSubscribe: View?, btnLogin: View?) {
        //引导图
        val list = arrayListOf<HighLight>()
        val element = HighLight(btnSubscribe, HighLight.Type.ROUND_RECTANGLE)
        element.round = 30
        list.add(element)
        val controller1 = NewbieGuide.with(this)
                .addHighLight(btnLogin, HighLight.Type.ROUND_RECTANGLE, 10)
                .setBackgroundColor(Color.parseColor("#88000000"))
                .setLabel("login")
                .setEveryWhereCancelable(true)
                .setLayoutRes(R.layout.activity_home_guide_layout)
                .alwaysShow(true)
                .build()
        val controller2 = NewbieGuide.with(this)
                .addHighLight(list)
                .setBackgroundColor(Color.parseColor("#88000000"))
                .setLabel("subscribe")
                .setEveryWhereCancelable(true)
                .setLayoutRes(R.layout.activity_home_guide_layout)
                .alwaysShow(true)
                .setOnGuideChangedListener(
                        object : OnGuideChangedListener {
                            override fun onRemoved(p0: Controller?) {
                                controller1.show()
                            }

                            override fun onShowed(p0: Controller?) {
                            }

                        }
                )
                .build()

        controller2.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }
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