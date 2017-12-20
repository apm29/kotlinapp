package com.apm29.network.api

import com.apm29.beanmodule.beans.HistoryContent
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by dingzhu on 2017/12/20.
 */
interface GankAPi {
    @GET("history/content/{pageSize}/{page}")
    fun getContent(@Path("pageSize") pageSize: Int, @Path("page") page: Int): Observable<HistoryContent>
}