package com.apm29.network.api

import com.apm29.beanmodule.beans.gank.RawResponse
import com.apm29.beanmodule.beans.zs.*
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by apm29 on 2017/9/5.
 */
interface API{
    interface Home {
        @GET("Home/initHomeViewData")
        fun  initHomeViewData(

        ): Observable<RawResponse<HomeViewData>>
    }
    interface Init {
        @GET("init/fetchIndustryCategory")
        fun fetchIndustryCategory(

        ):Observable<RawResponse<IndustryInfo>>
    }
    interface Login{
        @FormUrlEncoded
        @POST("Login/login")
        fun login(
                @Field("mobile")account: String,
                @Field("password") password:String,
                @Field("isWeixinBind") isWexinBind:Int
        ):Observable<LoginResult>

        @GET("User/initUserInfo")
        fun initUserInfo(
                @Query("userID") userID:Int
        ):Observable<RawResponse<UserInfo>>
    }
    interface Subscription{
        @GET("Subscription/fetchMySubscriptions")
        fun fetchMySubscription(@Query("userID")userID: Int):Observable<RawResponse<List<SubscriptionInfo>>>
    }
}