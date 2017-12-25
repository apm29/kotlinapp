package com.apm29.network.cache

import android.content.Context
import android.text.TextUtils
import com.apm29.beanmodule.beans.ding.AppConfig
import com.apm29.beanmodule.beans.zs.UserInfo


/**
 * Created by apm29 on 2017/9/8.
 */
class AccountCache {
    companion object {
        var userInfo: UserInfo? = null
        private val USER_INFO = "USER_INFO"
        var token:String =""
        private val TOKEN = "TOKEN"
        var appConfig: AppConfig? = null
        private val APP_CONFIG = "APP_CONFIG"

        fun saveUserInfo(context: Context, userInfo: UserInfo?) {
            if (userInfo != null) {
                //内存缓存
                this.userInfo = userInfo
                //文件
                ACache.get(context).put(USER_INFO, userInfo)
            }
        }

        fun getUserInfo(context: Context): UserInfo? {
            return if (AccountCache.userInfo != null) {
                this.userInfo
            } else {
                val mCache = ACache.get(context)
                AccountCache.userInfo = mCache.getAsObject(USER_INFO) as UserInfo?
                AccountCache.userInfo
            }
        }
        fun saveToken(context: Context, token: String?) {
            if (!TextUtils.isEmpty(token)) {
                //内存缓存
                this.token = token!!
                //文件
                ACache.get(context).put(TOKEN, token)
            }
        }

        fun getToken(context: Context?): String {
            if (context==null)return  ""
            return if (!TextUtils.isEmpty(this.token)) {
                this.token
            } else {
                val mCache = ACache.get(context)
                AccountCache.token = mCache.getAsString(TOKEN)?:""
                AccountCache.token
            }
        }

        fun saveAppConfig(context: Context, appConfig: AppConfig?) {
            if (appConfig != null) {
                //内存缓存
                this.appConfig = appConfig
                //文件
                ACache.get(context).put(APP_CONFIG, appConfig)
            }
        }

        fun getAppConfig(context: Context): AppConfig? {
            return if (AccountCache.appConfig != null) {
                this.appConfig
            } else {
                val mCache = ACache.get(context)
                AccountCache.appConfig = mCache.getAsObject(APP_CONFIG) as AppConfig?
                AccountCache.appConfig
            }
        }

    }
}