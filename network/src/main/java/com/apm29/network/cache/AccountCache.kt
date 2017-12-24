package com.apm29.network.cache

import android.content.Context
import com.apm29.beanmodule.beans.main.UserInfo


/**
 * Created by apm29 on 2017/9/8.
 */
class AccountCache {
    companion object {
        var userInfo: UserInfo? = null
        private val USER_INFO = "USER_INFO"

        fun saveUserInfo(context: Context, userInfo: UserInfo?) {
            if (userInfo != null) {
                //内存缓存
                this.userInfo = userInfo
                //文件
                ACache.get(context).put(USER_INFO, userInfo)
            }
        }

        fun getUserInfo(context: Context): UserInfo? {
            if (AccountCache.userInfo != null) {
                return this.userInfo
            } else {
                val mCache = ACache.get(context)
                AccountCache.userInfo = mCache.getAsObject(USER_INFO) as UserInfo?
                return AccountCache.userInfo
            }
        }


    }
}