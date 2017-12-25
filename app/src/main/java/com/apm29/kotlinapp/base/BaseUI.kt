package com.apm29.kotlinapp.base

import io.reactivex.disposables.CompositeDisposable

/**
 * Created by apm29 on 2017/9/7.
 */

interface BaseUI {
    //请求的订阅链接,在页面销毁时提前取消订阅
    var mDisposables:CompositeDisposable
    fun startLoading()
    fun stopLoading()
    fun onError(error: String?)
    fun onNewData(data: Any?)
    fun onEmpty()
}
