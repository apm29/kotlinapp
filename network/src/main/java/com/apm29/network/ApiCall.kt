package com.apm29.network

import android.content.Context
import android.os.Environment
import com.apm29.network.okhttpsetup.PersistentCookieJar
import com.apm29.network.okhttpsetup.SetCookieCache
import com.apm29.network.okhttpsetup.SharedPrefsCookiePersistor
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.HttpCookie
import java.net.URLDecoder
import java.util.concurrent.TimeUnit


/**
 * 网络配置相关
 * Created by apm29 on 2017/9/5.
 */
class ApiCall {
    /**
     * 配置
     */
    object Config {
        var useCache: Boolean = false          //是否使用缓存
        val CACHE_SIZE: Long = 5 * 1024 * 1024    //默认缓存上限
        val TIME_OUT: Long = 5000             //默认超时时间ms
        val DEBUG: Boolean = false
    }

    companion object {
        /**控制debug模式**/
        private val DEBUG = Config.DEBUG
        var versionCode = 0
        /**
         * 各类主机地址
         */
        /**main url**/
        private val main = if (DEBUG) "http://test.api.zhaosha.com/v3/" else "https://api.zhaosha.com/v3/"
        /**
         * 获取版本号
         */
        private fun getVersion(context: Context) =
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode

        private fun retrofit(baseUrl: String, context: Context): Retrofit {
            versionCode= getVersion(context)
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient(context))
                    .build()
        }

        private fun getOkHttpClient(context: Context): OkHttpClient {
            //file
            val file: File = Environment.getDownloadCacheDirectory()
            val size: Long = Config.CACHE_SIZE
            val cookieJar: CookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
            val  logInterceptor= HttpLoggingInterceptor();
            logInterceptor.level=HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                    .connectTimeout(Config.TIME_OUT, TimeUnit.MILLISECONDS)
                    .cookieJar(if (Config.useCache) CookieJar.NO_COOKIES else cookieJar)
                    .cache(Cache(file, size))
                    //设置拦截器
                    .addInterceptor { chain ->
                        //返回新请求
                        createNewRequest(chain!!)
                    }
                    .addInterceptor { chain->
                        logInterceptor.intercept(chain)
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
        fun mainService(context: Context): Retrofit = retrofit(main, context)

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
                    .addQueryParameter("version", ApiCall.versionCode.toString())

            // 新的请求
            val newRequest = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(url.build())
                    .build()
            return chain.proceed(newRequest)
        }
    }



}
