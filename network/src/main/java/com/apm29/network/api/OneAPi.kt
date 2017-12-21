package com.apm29.network.api

import com.apm29.beanmodule.beans.DataList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by dingzhu on 2017/12/20.
 */
interface OneAPi {
    //http://v3.wufazhuce.com:8000/api/onelist/idlist/?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android
    @GET("idlist")
    fun getContent(@Query("channel") channel: String = "wdj",
                   @Query("version") version: String = "4.0.2",
                   @Query("uuid") uuid: String,
                   @Query("platform") platform: String = "android`"
    ): Observable<DataList>
}