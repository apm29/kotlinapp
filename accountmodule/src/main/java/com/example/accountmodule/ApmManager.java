package com.example.accountmodule;

import android.support.annotation.NonNull;

import com.example.accountmodule.base.IAccountManager;
import com.example.accountmodule.base.IHttpClient;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public class ApmManager implements IAccountManager {

    private static ApmManager instance = new ApmManager();
    private IHttpClient client;
    private AccountBehavior behavior;

    private ApmManager() {

    }

    public static  ApmManager getInstance() {
        return instance;
    }

    @Override
    public IAccountManager setClient(IHttpClient client) {
        this.client = client;
        return this;
    }


    @Override
    public IAccountManager setAccountBehavior(AccountBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    @Override
    public <T extends Object> T setup(Class<T> tClass) {

        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return null;
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
