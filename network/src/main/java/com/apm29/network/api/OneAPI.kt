package com.apm29.network.api

import com.apm29.beanmodule.beans.one.DataList
import com.apm29.beanmodule.beans.one.OneList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by dingzhu on 2017/12/20.
 */
interface OneAPI {
    //http://v3.wufazhuce.com:8000/api/onelist/idlist/?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android
    @GET("idlist")
    fun getIdList(
            @Query("uuid") uuid: String
    ): Observable<DataList>

    //上面获取的data + /0?cchannel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android
    @GET("{data}/0")
    fun getContent(
           @Path("data") data:String
    ): Observable<OneList>
}