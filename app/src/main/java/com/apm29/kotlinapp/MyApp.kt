package com.apm29.kotlinapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.apm29.network.cache.AccountCache


/**
 * Created by apm29 on 2017/9/5.
 */
class MyApp: Application(){
    override fun onCreate() {
        super.onCreate()
        registerLifeCircle()
        init()
    }
    companion object {
        var count:Int=0
    }
    private fun registerLifeCircle() {
        registerActivityLifecycleCallbacks(
                object :ActivityLifecycleCallbacks{
                    override fun onActivityPaused(activity: Activity?) {
                    }

                    override fun onActivityResumed(activity: Activity?) {
                    }

                    override fun onActivityStarted(activity: Activity?) {
                        if (count==0){
                            AccountCache.getUserInfo(this@MyApp)!=null
                            AccountCache.userInfo=AccountCache.getUserInfo(this@MyApp)
                        }
                        count++
                    }

                    override fun onActivityDestroyed(activity: Activity?) {
                    }

                    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                    }

                    override fun onActivityStopped(activity: Activity?) {
                        count--
                    }

                    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                    }

                }
        )
    }

    private fun init() {
        /**
         * 登录持久化
         */
        val userInfo = AccountCache.getUserInfo(this)
        if ( userInfo !=null){
           AccountCache.userInfo=userInfo
       }
    }
}