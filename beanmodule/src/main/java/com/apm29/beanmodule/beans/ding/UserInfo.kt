package com.apm29.beanmodule.beans.ding

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(val loginTypeTip: String = "",
                    val loginType: Int = 0,
                    val cardNoAuth: Int = 0,
                    val mobile: String = "",
                    val avatar: String = "",
                    val pmCount: Int = 0,
                    val cardNo: String = "",
                    val toWallet: Int = 0,
                    val realName: String = "",
                    val wStatus: Int = 0,
                    val bankCardNum: Int = 0,
                    @SerializedName("product7Tip")
                    val productTip: Boolean = false,
                    val id: Int = 0,
                    val payPassword: String = "",
                    val username: String = ""):Serializable