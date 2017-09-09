package com.apm29.kotlinapp

import android.app.Application
import com.apm29.network.cache.AccountCache


/**
 * Created by apm29 on 2017/9/5.
 */
class MyApp: Application(){
    override fun onCreate() {
        super.onCreate()
        init()
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