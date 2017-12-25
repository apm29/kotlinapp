package com.apm29.beanmodule.beans.ding

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppConfig(val loginRewardBanner: LoginRewardBanner?,
                     @SerializedName("loginInfoV2")
                  val loginInfoV: String = "",
                     @SerializedName("registerSuccessRewardInfoV2")
                  val registerSuccessRewardInfoV: String = "",
                     val loginBannerUrl: String = "",
                     val activityPopupSwitch: Int = 0,
                     val investUrl: String = "",
                     val registerBanner: RegisterBanner?,
                     @SerializedName("platformMonitorInfoV2")
                  val platformMonitorInfoV: String = "",
                     @SerializedName("registerRewardInfoV2")
                  val registerRewardInfoV: String = "",
                     val maxLimitDayBankName: String = "",
                     val investButtonText: String = "",
                     val couponSwitch: Int = 0,
                     val activityPopup: ActivityPopup?,
                     val noviceProjectInterestInfo: String = "",
                     @SerializedName("registerSuccessStepRewardInfoV2")
                  val registerSuccessStepRewardInfoV: String = "",
                     val accountMonitor: String = "",
                     val registerSuccessNoviceProjectId: Int = 0,
                     val registerRewardAmountInfo: String = "",
                     val loginBanner: LoginBanner?,
                     val loginRewardBannerUrl: String = "",
                     @SerializedName("new15DayUserProjectId")
                  val newDayUserProjectId: Int = 0,
                     @SerializedName("platformAccountInfoV2")
                  val platformAccountInfoV: String = "",
                     val registerRewardTextInfo: String = "",
                     val registerSuccessRewardTextInfo: String = ""):Serializable