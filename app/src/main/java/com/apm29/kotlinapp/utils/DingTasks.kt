package com.apm29.kotlinapp.utils

import android.content.Context
import cn.jpush.android.api.JPushInterface
import com.apm29.beanmodule.beans.ding.ActivityPopupDetail
import com.apm29.beanmodule.beans.ding.BaseResponse
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.network.ApiCall
import com.apm29.network.api.DingAPI
import com.apm29.network.cache.AccountCache
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by dingzhu on 2017/12/25.
 */
object DingTasks {
    /**
     * 默认处理
     */
    private fun <T> defaultTask(observable: Observable<BaseResponse<T>>, listener: BaseUI): Disposable {
        val onSuccess: (BaseResponse<T>) -> Unit = {
            if (it.success) {
                listener.onNewData(it.result)
            } else {
                listener.onError(it.errorMsg)
            }
            listener.stopLoading()
        }
        val onError: (Throwable) -> Unit = {
            listener.onError(it.message)
            listener.stopLoading()
        }
        return observable
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        onSuccess,
                        onError
                ).also { listener.mDisposables.add(it) }
    }
    /**
     * 获取全局设置AppConfig
     */
    fun queryAppConfig(context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        return  defaultTask(ApiCall.dingApi(context)
                .create(DingAPI::class.java).queryAppConfig(JPushInterface.getRegistrationID(context)),listener)

    }

    /**
     * app弹窗详情
     */
    fun activityPopupMessage(messageId: Int, context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        val observable = ApiCall.dingApi(context)
                .create(DingAPI::class.java).activityPopupMessage(messageId, JPushInterface.getRegistrationID(context))
        return defaultTask(observable, listener)
    }


    /**
     * 启动页详情
     */
    fun getStartupPage(context: Context, listener: BaseUI): Disposable {
        listener.startLoading()
        return defaultTask(ApiCall.dingApi(context)
                .create(DingAPI::class.java).getStartupPage(JPushInterface.getRegistrationID(context)),listener)
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
        return defaultTask(ApiCall.dingApi(context)
                .create(DingAPI::class.java).userInfo(JPushInterface.getRegistrationID(context)),listener)
    }
    fun queryInProgressProjects(context: Context,listener: BaseUI):Disposable{
        return defaultTask(
                ApiCall.dingApi(context).create(DingAPI::class.java).queryProgressProjects(JPushInterface.getRegistrationID(context)),
                listener
        )
    }

}
