package com.apm29.kotlinapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import cn.jpush.android.api.JPushInterface
import com.apm29.beanmodule.beans.ding.UserInfo
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.base.SimpleBaseUI
import com.apm29.kotlinapp.utils.DingTasks
import com.apm29.kotlinapp.utils.toPx
import com.apm29.network.cache.AccountCache
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary


/**
 * Created by apm29 on 2017/9/5.
 */
class MyApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        try {
            LeakCanary.install(this)
        } catch (e: Exception) {
            e.printStackTrace()

        }
        instance = this
        registerLifeCircle()
        init()
    }

    companion object {
        var count: Int = 0
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context

        fun getApplication(): Context {
            return instance
        }
    }

    private fun registerLifeCircle() {
        registerActivityLifecycleCallbacks(
                object : ActivityLifecycleCallbacks {
                    override fun onActivityPaused(activity: Activity?) {
                    }

                    override fun onActivityResumed(activity: Activity?) {
                    }

                    override fun onActivityStarted(activity: Activity?) {
                        if (count == 0) {
                            //ding
                            if (activity != null)
                                DingTasks.checkToken(activity, object : SimpleBaseUI() {
                                    override fun onNewData(data: Any?) {
                                        if (data is String)
                                            DingTasks.userInfo(activity, this)
                                        else if (data is UserInfo){
                                            AccountCache.saveUserInfo(activity,data)
                                        }
                                    }
                                })
                        }
                        count++
                    }

                    override fun onActivityDestroyed(activity: Activity?) {
                        count--
                    }

                    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                    }

                    override fun onActivityStopped(activity: Activity?) {

                    }

                    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                    }

                }
        )
    }

    private fun init() {
        /**
         * jpush
         */
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        val registrationID = JPushInterface.getRegistrationID(this)
        println("J_PUSH" + "registrationID = ${registrationID}")
        /**
         * 登录持久化
         */
        val userInfo = AccountCache.getUserInfo(this)
        if (userInfo != null) {
            AccountCache.userInfo = userInfo
        }

        /**
         * stetho初始化
         */
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}