package com.apm29.kotlinapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.network.Network
import com.apm29.network.api.Home
import com.apm29.network.api.Init
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class HomeActivity : BaseActivity<HomePresenter>() {
    override fun  getPresenter(): HomePresenter = HomePresenter(this)

    private var subscribe: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_layout)
        subscribe=(getPresenter()).loadNetData()


    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }
}
class HomePresenter(ui: BaseUI) : BasePresenter(ui) {
    fun loadNetData(): Disposable {
       return  Network.mainService(ui as Context)
                .create(Home::class.java)
                .initHomeViewData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .filter {
                    if (it.meta.code != 200) {
                        val name = Thread.currentThread().name
                        (ui as Activity ).runOnUiThread { Toast.makeText((ui as Activity ), name, Toast.LENGTH_LONG).show() }
                    }
                    return@filter it.meta.code == 200
                }
                .map {
                    Observable.fromIterable(it.data.ads)
                            .subscribe {
                                println(Thread.currentThread().name)
                                println(it)
                            }
                    Observable.just(it.data.skuNum)
                            .subscribe {
                                println(Thread.currentThread().name)
                                println(it)
                            }
                    Observable.fromIterable(it.data.recommendWTB)
                            .subscribe {
                                println(Thread.currentThread().name)
                                println(it)
                            }
                }.subscribe()
    }

    fun fetchIndustry(): Disposable {
        return Network.mainService((ui as Activity ))
                .create(Init::class.java)
                .fetchIndustryCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}