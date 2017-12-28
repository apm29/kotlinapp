package com.apm29.kotlinapp.base

import io.reactivex.disposables.CompositeDisposable

/**
 * Created by apm29 on 2017/12/26.
 */
 open class SimpleBaseUI:BaseUI {
    override var mDisposables: CompositeDisposable =CompositeDisposable()

    override fun startLoading() {
    }

    override fun stopLoading() {
    }

    override fun onError(error: String?) {
    }

    override fun onNewData(data: Any?) {
    }

    override fun onEmpty() {
    }
}