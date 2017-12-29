package com.apm29.kotlinapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.apm29.beanmodule.beans.ding.UserInfo
import com.apm29.kotlinapp.base.SimpleBaseUI
import com.apm29.kotlinapp.utils.DingTasks
import com.apm29.network.cache.AccountCache
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * application
 * Created by apm29 on 2017/9/5.
 */
class MyApp : MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

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
                                        else if (data is UserInfo) {
                                            AccountCache.saveUserInfo(activity, data)
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
        /**
         * bugly init
         */
        val context = applicationContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName = getProcessName(android.os.Process.myPid())
        // 设置是否为上报进程
        val strategy = UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
        // 初始化Bugly
        CrashReport.initCrashReport(context, "b09d39ffdb", BuildConfig.DEBUG, strategy)
        // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
        // CrashReport.initCrashReport(context, strategy);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim({ it <= ' ' })
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

        }
        return null
    }
}