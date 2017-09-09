package com.apm29.network

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 网络配置相关
 * Created by apm29 on 2017/9/5.
 */
class Network {
    companion object  {
        /**控制debug模式**/
        private val DEBUG = true


        var versionCode = 0
        /**main url**/
        val main = if (DEBUG) "http://test.api.zhaosha.com/v3/" else "https://api.zhaosha.com/v3/"
        private val mainService: Retrofit by lazy {
            return@lazy retrofit(main)
        }
        private fun retrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .client(OkHttpClient.Builder().
                            //设置拦截器
                            addInterceptor { chain ->
                                //返回新请求
                                createNewRequest(chain!!)
                            }
                            .build()
                    )
                    .build()
        }
        fun mainService(context: Context): Retrofit {
            if (versionCode <= 0) {
                versionCode = getVersion(context)
            }
            return mainService
        }

        private fun getVersion(context: Context) =
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    }

}
@Throws(Exception::class)
//为http拦截器添加公共的参数
private fun createNewRequest(chain: Interceptor.Chain): Response {
    //原req
    val oldRequest = chain.request()
    //新url builder
    val url = oldRequest
            .url()
            .newBuilder()
            .scheme(oldRequest.url().scheme())
            .host(oldRequest.url().host())
            .addQueryParameter("platform", "1")
            .addQueryParameter("version", Network.versionCode.toString())

    // 新的请求
    val newRequest = oldRequest.newBuilder()
            .method(oldRequest.method(), oldRequest.body())
            .url(url.build())
            .build()
    return chain.proceed(newRequest)
}