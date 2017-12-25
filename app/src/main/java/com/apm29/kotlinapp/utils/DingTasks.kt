package com.apm29.kotlinapp.utils

import android.content.Context
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.network.ApiCall
import com.apm29.network.api.DingAPI
import com.apm29.network.cache.AccountCache
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by dingzhu on 2017/12/25.
 */
object DingTasks {
    /**
     * 获取全局设置AppConfig
     */
    fun queryAppConfig(context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        return ApiCall.dingApi(context)
                .create(DingAPI::class.java).queryAppConfig()
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            if (it.success) {
                                listener.onNewData(it.result)
                                AccountCache.saveAppConfig(context, it.result)//存储全局设置
                            } else {
                                listener.onError(it.errorMsg)
                            }
                            listener.stopLoading()
                        },
                        {
                            listener.onError(it.message)
                            listener.stopLoading()
                        }
                ).also {
                        listener.mDisposables.add(it)
                }
    }

    fun activityPopupMessage(messageId:Int,context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        return ApiCall.dingApi(context)
                .create(DingAPI::class.java).activityPopupMessage(messageId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            if (it.success) {
                                listener.onNewData(it.result)
                            } else {
                                listener.onError(it.errorMsg)
                            }
                            listener.stopLoading()
                        },
                        {
                            listener.onError(it.message)
                            listener.stopLoading()
                        }
                ).also {listener.mDisposables.add(it)}
    }
}
