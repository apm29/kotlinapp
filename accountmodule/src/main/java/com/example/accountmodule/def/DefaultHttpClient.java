package com.example.accountmodule.def;


import com.example.accountmodule.base.IHttpClient;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public class DefaultHttpClient implements IHttpClient {


    private String url;

    @Override
    public IHttpClient setBaseUrl(String url) {
        this.url = url;

        return this;
    }

    @Override
    public IHttpClient addParameter(String... param) {
        return this;
    }



}
