package com.apm29.network.okhttpsetup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ━━━━ Code is far away from ━━━━━━
 * 　　  () 　　　  ()
 * 　　  ( ) 　　　( )
 * 　　  ( ) 　　　( )
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　┻　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━ bug with the XYY protecting━━━
 * 特点：① 支持无网络时数据缓存，无需服务器的支持
 * ② 支持过滤恶意频繁网络请求
 * ③ 支持cookie头数据的自动加载及持久化
 */
public class OkHttpEngine {

    /**
     * 缓存的默认大小
     */
    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 5; //5M

    /**
     * 缓存的默认文件夹
     */
    private static final String DEFAULT_CACHE_FILE = "okhttp_cache";
    private static PersistentCookieJar cookieJar;
    private static OkHttpClient okHttpClient;
    private static OkHttpEngine okHttpEngine = new OkHttpEngine();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OkHttpEngine() {
    }

    public static OkHttpEngine getInstance(Context context) {
        if (okHttpClient == null) {
            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    //支持自动持久化cookie和自动添加cookie
                    .cookieJar(cookieJar)
                    //没有网络，加载缓存+
                    .addInterceptor(new ForceCacheInterceptor(context))
                    //过滤频繁请求，10为缓存时间，单位秒,10秒之内反复请求，取缓存，超出10秒，取服务器数据
                    .addNetworkInterceptor(new FilterFastRequestInterceptor(10))
                    .cache(new Cache(new File(context.getCacheDir(), DEFAULT_CACHE_FILE), DEFAULT_CACHE_SIZE))

                    .build();
        }
        return okHttpEngine;
    }

    /**
     * 没有参数的get请求
     *
     * @param url
     * @param code     请求码，区分同一类类中不同的请求
     * @param callback
     */
    public void get(String url, int code, HttpCallback callback) {
        get(url, null, code, callback);
    }

    /**
     * 有参数的get请求，参数包括headers
     *
     * @param url
     * @param httpParams 封装请求参数
     * @param code       请求码，区分同一类类中不同的请求
     * @param callback
     */
    public void get(String url, HttpParams httpParams, final int code, final HttpCallback callback) {
        //构建RequestBuilder对象
        Request.Builder builder = new Request.Builder()
                .get();
        if (httpParams != null) {
            //添加header
            Map<String, String> headers = httpParams.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            //拼接get请求参数
            url = url + httpParams.toGetParams();
        }

        Request request = builder.url(url).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.error(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null && callback != null) {
                    final String data = body.string();
                    if (data != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.success(code, data);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 上传表单数据
     *
     * @param url
     * @param httpParams 封装请求参数
     * @param code       请求码，区分同一类类中不同的请求
     * @param callback
     */
    public void post(String url, HttpParams httpParams, final int code, final HttpCallback callback) {
        if (httpParams == null) {
            throw new IllegalArgumentException("请设置提交参数");
        }
        //构建RequestBuilder对象
        Request.Builder builder = new Request.Builder()
                .url(url);

        //创建请求体,formBody表单
        Map<String, String> params = httpParams.getParams();
        FormBody.Builder formBodybuilder = new FormBody.Builder();
        if (params != null) {
            Iterator i = params.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                formBodybuilder.add(key, params.get(key));
            }
        }
        FormBody formBody = formBodybuilder.build();

        builder.post(formBody);

        //添加header
        Map<String, String> headers = httpParams.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //构建Request对象
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.error(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null && callback != null) {
                    final String data = body.string();
                    if (data != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.success(code, data);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 上传文件
     *
     * @param url
     * @param key      文件对应的参数key值
     * @param file     待上传的文件
     * @param code     请求码，区分同一类类中不同的请求
     * @param callback
     */
    public void postFile(String url, String key, File file, final int code, final HttpCallback callback) {
        if (file == null) {
            throw new IllegalArgumentException("请设置上传文件");
        }
        //构建RequestBuilder对象
        Request.Builder builder = new Request.Builder();

        //构建MultipartBody
        MultipartBody.Builder multipatBodyBuilder = new MultipartBody.Builder();
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        multipatBodyBuilder.addFormDataPart(key, key, fileBody);

        builder.post(multipatBodyBuilder.build());
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.error(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null && callback != null) {
                    final String data = body.string();
                    if (data != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.success(code, data);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 退出登录时，清除cookie数据
     */
    public void logout() {
        cookieJar.clear();
    }

    public interface HttpCallback {
        void success(int code, String data);

        void error(Throwable t);
    }
}
