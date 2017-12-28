package com.apm29.network.api

import com.apm29.beanmodule.beans.ding.*
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by dingzhu on 2017/12/25.
 */
interface DingAPI{
    /**
     * isEnc	N
     * versionCode	11
     * token	VEtfMjAxNzExMjIyMDA1NThfMTI4MV81ODEwOTU=
     * deviceType	2
     * timestamp	1514173759930
     * deviceSerialId	1e9c32cad74861fa
     * supperUserId
     * phoneSystemVersion	7.0
     * versionName	2.1.0
     * appType	1001
     * registrationId	18071adc030932941d6
     * channel	default
     * deviceUniqueId	eab3f234
     */
    //http://app-api.dinglc.com.cn/rest/message/queryAppConfig.json
    @FormUrlEncoded
    @POST("message/queryAppConfig.json")
    fun queryAppConfig(
        @Field("registrationId") registrationId:String = ""
    ): Observable<BaseResponse<AppConfig>>

    @FormUrlEncoded
    @POST("message/activityPopupMessage.json")
    fun activityPopupMessage(
            @Field("messageId") messageId:Int,
            @Field("registrationId") registrationId:String = ""
    ): Observable<BaseResponse<ActivityPopupDetail>>

    @FormUrlEncoded
    @POST("message/getStartupPage.json")
    fun getStartupPage(
            @Field("registrationId")registrationID: String? =""
    ): Observable<BaseResponse<StartupPage>>

    @FormUrlEncoded
    @POST("user/checkToken.json")
    fun checkToken(
            @Field("registrationId")registrationID: String? =""
    ): Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("user/userInfo.json")
    fun userInfo(
            @Field("registrationId")registrationID: String?
    ): Observable<BaseResponse<UserInfo>>
}