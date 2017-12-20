package com.apm29.network.api

import com.apm29.beanmodule.Init.HistoryContent
import com.apm29.beanmodule.Init.RawResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by dingzhu on 2017/12/20.
 */
interface GankAPi {
    @GET("history/content/{page}/{pageSize}")
    fun getContent(@Path("page") page: Int, @Path("pageSize") pageSize: Int): Observable<HistoryContent>
}