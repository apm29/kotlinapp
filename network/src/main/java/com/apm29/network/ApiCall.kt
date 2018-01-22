package com.apm29.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.apm29.network.cache.AccountCache
import com.apm29.network.okhttpsetup.PersistentCookieJar
import com.apm29.network.okhttpsetup.SetCookieCache
import com.apm29.network.okhttpsetup.SharedPrefsCookiePersistor
import com.apm29.network.utils.NetworkUtils
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.TimeUnit


/**
 * 网络配置相关
 * Created by apm29 on 2017/9/5.
 */
class ApiCall {
    @SuppressLint("StaticFieldLeak")
    /**
     * 配置单例
     */
    object Config {
        var useCache: Boolean = false          //是否使用缓存
        val CACHE_SIZE: Long = 5 * 1024 * 1024    //默认缓存上限
        val TIME_OUT: Long = 50000             //默认超时时间ms
        val DEBUG: Boolean = false
        var APP:Context?=null
    }

    companion object {

        /**控制debug模式**/
        private val DEBUG = Config.DEBUG
        var versionCode = 0
        var versionName = ""

        var mainRetro: Retrofit? = null
        var dingRetro: Retrofit? = null
        var gankRetro: Retrofit? = null
        var oneRetro:Retrofit?=null
        /**
         * 各类主机地址
         */
        /**main url**/
        private val main = "https://api.zhaosha.com/v3/"
        private val ding =  "http://app-api.dinglc.com.cn:9999/rest/"
        private val gank = "http://gank.io/api/"
        private val oneList = "http://v3.wufazhuce.com:8000/api/onelist/"
        /**
         * 获取版本号
         */
        private fun getVersion(context: Context) =
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        /**
         * 获取版本
         */
        private fun getVersionName(context: Context) =
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
        fun getApp() =Config.APP
        private fun retrofit(baseUrl: String, context: Context): Retrofit {
            ApiCall.Config.APP=context.applicationContext
            versionCode = getVersion(context)
            versionName = getVersionName(context)
            val retro: Retrofit?=
                    when (baseUrl) {

                        main -> if (mainRetro == null) Retrofit.Builder()
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(baseUrl)
                                .client(getOkHttpClient(context))
                                .build().also { mainRetro = it } else mainRetro
                        ding -> if (dingRetro == null) Retrofit.Builder()
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(baseUrl)
                                .client(getOkHttpClient(context))
                                .build().also { dingRetro = it } else dingRetro
                        gank -> if (gankRetro == null) Retrofit.Builder()
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(baseUrl)
                                .client(getOkHttpClient(context))
                                .build().also { gankRetro = it }else gankRetro
                        oneList->
                            if (oneRetro == null) Retrofit.Builder()
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .baseUrl(baseUrl)
                                    .client(getOkHttpClient(context))
                                    .build().also { oneRetro = it }else oneRetro
                        else -> mainRetro
                    }
            return retro!!
        }

        private fun getOkHttpClient(context: Context): OkHttpClient {
            //file
            val file: File = Environment.getDownloadCacheDirectory()
            val size: Long = Config.CACHE_SIZE
            val cookieJar: CookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
            val logInterceptor = HttpLoggingInterceptor {
                try {
                    val text = URLDecoder.decode(it, "utf-8")
                    Log.d("OkHttp-->", text)
                } catch (e: Exception) {
                    Log.d("OkHttp-->", it)
                }
            }
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                    .connectTimeout(Config.TIME_OUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Config.TIME_OUT,TimeUnit.MILLISECONDS)
                    .cookieJar(if (Config.useCache) CookieJar.NO_COOKIES else cookieJar)
                    .cache(Cache(file, size))
                    //设置拦截器
                    .addInterceptor { chain ->
                        //添加公共headers
                       addPublicHeader(chain)
                       // chain.proceed(chain.request())
                    }
                    .addInterceptor { chain ->
                        logInterceptor.intercept(chain)//logger
                    }
                    .addNetworkInterceptor { chain ->
                        //处理返回的chain
                        processRawResult(chain)
                        //chain.proceed(chain.request())
                    }
                    .build()
        }

        /**
         * 后处理
         */
        private fun processRawResult(chain: Interceptor.Chain): Response? {
            // TODO
            val response = chain.proceed(chain.request())
            println("response = ${response}")

            return response
        }

        /**
         * main api
         */
        fun mainApi(context: Context): Retrofit = retrofit(main, context)

        /**
         * ding api
         */
        fun dingApi(context: Context): Retrofit = retrofit(ding, context)

        /**
         * gank.id/api
         */
        fun gankApi(context: Context): Retrofit {
            return retrofit(gank, context)
        }

        /**
         * one api
         */
        fun oneApi(context: Context): Retrofit {
            return retrofit(oneList,context)
        }

        @Throws(Exception::class)
        //为http拦截器添加公共的参数
        private fun addPublicHeader(chain: Interceptor.Chain): Response {
            //原req
            val oldRequest = chain.request()
            //新url builder
            val url = oldRequest
                    .url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
                    //加入公共参数
                    .also {   //one api
                        it  .addQueryParameter("channel","wdj")
                            .addQueryParameter("version",ApiCall.versionCode.toString())
                            .addQueryParameter("platform", "android")
                            .addQueryParameter("timestamp",System.currentTimeMillis().toString())
                    }
            val requestBody = oldRequest.body()

            val newRequestBody =FormBody.Builder()
            if (requestBody is FormBody){
                var i=0
                while (i<requestBody.size()){//加入原有post参数
                    newRequestBody.add(
                            requestBody.encodedName(i),
                            requestBody.encodedValue(i)
                    )
                    i++
                }
            }
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
            //加入新的公共post参数
            newRequestBody
                    .add("isEnc","N")
                    .add("versionCode", ApiCall.versionCode.toString()+3)
                    .add("token", AccountCache.getToken(getApp()))
                    .add("deviceType","2")
                    .add("timestamp",System.currentTimeMillis().toString())
                    .add("deviceSerialId",NetworkUtils.getAndroidId(Config.APP))
                    .add("supperUserId","")
                    .add("phoneSystemVersion",Build.VERSION.RELEASE + "")
                    .add("versionName",ApiCall.versionName)
                    .add("appType","1001")
                    .add("channel","default")
                    .add("deviceUniqueId",Build.SERIAL)

            // 新的请求
            val newRequest = oldRequest.newBuilder()
                    .post(newRequestBody.build())
                    .method(oldRequest.method(), if(oldRequest.method()=="GET")null else newRequestBody.build())
                    .url(url.build())
                    .build()
            return chain.proceed(newRequest)
        }
    }


}
