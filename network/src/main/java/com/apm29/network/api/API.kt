package com.apm29.network.api

import com.apm29.beanmodule.Init.HomeViewData
import com.apm29.beanmodule.Init.IndustryInfo
import com.apm29.network.Network
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by apm29 on 2017/9/5.
 */
interface Home {
    @GET("Home/initHomeViewData")
    fun  initHomeViewData(

    ): Observable<HomeViewData>
}
interface Init {
    @GET("init/fetchIndustryCategory")
    fun fetchIndustryCategory(

    ):Observable<IndustryInfo>
}