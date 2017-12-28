package com.apm29.kotlinapp.utils

import android.content.Context
import cn.jpush.android.api.JPushInterface
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
                .create(DingAPI::class.java).queryAppConfig(JPushInterface.getRegistrationID(context))
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

    /**
     * app弹窗详情
     */
    fun activityPopupMessage(messageId: Int, context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        return ApiCall.dingApi(context)
                .create(DingAPI::class.java).activityPopupMessage(messageId, JPushInterface.getRegistrationID(context))
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
                ).also { listener.mDisposables.add(it) }
    }

    /**
     * 启动页详情
     */
    fun getStartupPage(context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        return ApiCall.dingApi(context)
                .create(DingAPI::class.java).getStartupPage(JPushInterface.getRegistrationID(context))
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
                ).also { listener.mDisposables.add(it) }
    }

    /**
     * 检查token
     */
    fun checkToken(context: Context, listener: BaseUI): Disposable {
        return ApiCall.dingApi(context)
                .create(DingAPI::class.java).checkToken()
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { it ->
                            if (it.success) {
                                showToast(it.result)
                                listener.onNewData(it.result)
                                AccountCache.saveToken(context, it.result)
                            } else {
                            }
                        },
                        {
                            showToast(it.message?:"连接失败")
                        }
                )
    }

    fun userInfo(context: Context, listener: BaseUI): Disposable {
        return ApiCall.dingApi(context)
                .create(DingAPI::class.java).userInfo(JPushInterface.getRegistrationID(context))
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
                ).also { listener.mDisposables.add(it) }
    }
}
