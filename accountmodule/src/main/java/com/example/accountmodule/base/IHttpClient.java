package com.example.accountmodule.base;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public interface IHttpClient {
    /**
     * 设置url
     */
    IHttpClient setBaseUrl(String url);
    /**
     * 传入参数
     */
    IHttpClient addParameter(String... param);

    /**
     *
     */
}
