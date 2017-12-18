package com.apm29.kotlinapp.ui

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.*
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
            println("NightVeil shown"+NightVeil.show("btnLogin", this))
        }
    }

    override fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
       mPresenter.loadNetData()
    }

    private fun showGuide(btnSubscribe: View?, btnLogin: View?) {
        //引导图
        //println("NightVeil："+NightVeil.removeAllController(this))
        val controller1
                = NightVeil.from("btnLogin",this).addFocus(Focus(btnLogin!!, null, Focus.TYPE.OVAL))

        NightVeil
                .from("btnSubscribe",this)
                .addFocus(Focus(btnSubscribe!!, object : Focus.HitFocusListener {
                    override fun onHit(focus: Focus): Boolean {
                        focus.view?.performClick()
                        controller1.show()
                        focus.removeSelf()
                        return false
                    }
                }))
                .addFocus(Focus(
                        RectF(400F,300F,600F,400F),
                        radius = 20F
                ))
                .addFocus(Focus(R.id.iv_logo,type = Focus.TYPE.CIRCLE,padding = 20))
                .setBackgroundColorRes(R.color.guide_bg_color)
                .setLayout(R.layout.activity_home_guide_layout)
                .addTransformer {
                    val container=it.findViewById<ConstraintLayout>(R.id.cl_container)
                    val logo = it.findViewById<ImageView>(R.id.iv_logo)
                    val tv = it.findViewById<TextView>(R.id.tv_guide)
                    val arrow = it.findViewById<ImageView>(R.id.arrow)
                    val va=ValueAnimator.ofFloat(0F,200F)
                    va.addUpdateListener {
                        val transition = it.animatedValue as Float
                        logo.x=300+transition
                        logo.y=300+transition
                        arrow.x=logo.x-logo.measuredWidth-20
                        arrow.y=logo.y
                        tv.x=arrow.x-tv.measuredWidth-20
                        tv.y=logo.y+(logo.measuredHeight-tv.measuredHeight)/2
                    }
                    va.duration = 2000
                    va.repeatCount=20
                    va.repeatMode=ValueAnimator.REVERSE
                    va.start()
                }
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

