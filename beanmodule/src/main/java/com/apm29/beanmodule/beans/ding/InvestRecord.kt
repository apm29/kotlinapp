package com.apm29.beanmodule.beans.ding

import com.google.gson.annotations.SerializedName

data class InvestRecord(val loginTypeTip: String = "",
                        val addTime: Long = 0,
                        val loginType: Int = 0,
                        val rechargeNo: String = "",
                        val sortTime: Long = 0,
                        val modifyUserId: Int = 0,
                        val cardNo: String = "",
                        val score: Int = 0,
                        val modifyTime: Long = 0,
                        val orderTime: Long = 0,
                        val statusNew: Int = 0,
                        @SerializedName("product7Tip")
                        val product7Tip: String = "",
                        val invSucc: Int = 0,
                        val id: Int = 0,
                        val projectTitle: String = "",
                        val deviceType: Int = 0,
                        val addUserId: Int = 0,
                        val bowType: Int = 0,
                        val showTime: Long = 0,
                        val invTotal: Int = 0,
                        val ghostPhone: String = "",
                        val userId: Int = 0,
                        val expireDayNum: Int = 0,
                        val autoInv: Int = 0,
                        val projectId: Int = 0,
                        val dueInterest: Double = 0.0,
                        val status: Int = 0)