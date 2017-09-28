package com.apm29.network.okhttpsetup;

import android.content.Context;

import com.apm29.network.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
        * 判断当前网络状况，如果有网络，则不强制加载缓存，否则强制加载缓存的拦截器
        * 仅限GET请求
        *
        */
public class ForceCacheInterceptor implements Interceptor {
    private Context mContext;

    public ForceCacheInterceptor(Context context) {
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtils.checkNetwork(mContext) && "GET".equals(request.method().toUpperCase())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            return chain.proceed(request);
        }
        return chain.proceed(request);
    }
}
