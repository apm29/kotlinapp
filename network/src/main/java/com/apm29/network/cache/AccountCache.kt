package com.apm29.network.cache

import android.content.Context
import com.apm29.beanmodule.Init.UserInfo


/**
 * Created by apm29 on 2017/9/8.
 */
class AccountCache {
    companion object {
        var userInfo: UserInfo? = null
        private val USERINFO = "USERINFO"

        fun saveUserInfo(context: Context, userInfo: UserInfo?) {
            if (userInfo != null) {
                //内存缓存
                this.userInfo = userInfo
                //文件
                ACache.get(context).put(USERINFO, userInfo)
            }
        }

        fun getUserInfo(context: Context): UserInfo? {
            if (AccountCache.userInfo != null) {
                return this.userInfo
            } else {
                val mCache = ACache.get(context)
                AccountCache.userInfo = mCache.getAsObject(USERINFO) as UserInfo?
                return AccountCache.userInfo
            }
        }


    }
}