package com.apm29.kotlinapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.apm29.beanmodule.Init.HomeViewData
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.ui.account.LoginActivity
import com.apm29.kotlinapp.ui.subscription.SubscriptionManagerActivity
import com.apm29.network.Network
import com.apm29.network.api.Home
import com.apm29.network.api.Init
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class HomeActivity : BaseActivity<HomePresenter>() {
    override fun <N : Any?> onNewData(data: N) {
        if (data is HomeViewData){
            val tvHello = findViewById(R.id.tv_hello) as TextView
            tvHello.text=data.toString()
        }


    }

    override fun onError(error: String) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        val tvHello = findViewById(R.id.tv_hello) as TextView
        tvHello.text=error
    }

    override fun getPresenter(): HomePresenter = HomePresenter(this)

    private var subscribe: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_layout)
        subscribe = mPresenter.loadNetData()

        findViewById(R.id.btn_login).setOnClickListener {
            LoginActivity.starter(this)
        }
        findViewById(R.id.btn_subscribe_mine).setOnClickListener {
            SubscriptionManagerActivity.starter(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }
}

class HomePresenter(ui: BaseUI) : BasePresenter(ui) {
    fun loadNetData(): Disposable {
        return Network.mainService(ui as Context)
                .create(Home::class.java)
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
                            println("result:"+it)
                            ui.onNewData(it.data)
                        },
                        {
                            println("error:"+it)
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
        return Network.mainService((ui as Activity))
                .create(Init::class.java)
                .fetchIndustryCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}