package com.apm29.kotlinapp.base

/**
 * Created by apm29 on 2017/9/7.
 */

interface BaseUI {
    fun startLoading()
    fun stopLoading()
    fun onError(error: String?)
    fun  onNewData(data: Any?)
    fun onEmpty()
}
