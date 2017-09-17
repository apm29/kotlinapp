package com.example.accountmodule;

import com.example.accountmodule.base.IAccountManager;
import com.example.accountmodule.base.IHttpClient;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public class AccountManager<T> implements IAccountManager<T>{
    private static final AccountManager ourInstance = new AccountManager();

    public static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {

    }

    @Override
    public IAccountManager setClient(IHttpClient client) {
        return this;
    }

    @Override
    public IAccountManager setResponseType(T responseType) {
        return this;
    }

    @Override
    public IAccountManager setAccountBehavior(AccountBehavior behavior) {
        return this;
    }

    @Override
    public T setup() {
        return null;
    }
}
