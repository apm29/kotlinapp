package com.example.accountmodule.base;

import com.example.accountmodule.AccountError;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public interface IResult<T> {
    void onFailure(AccountError error);
    void onSuccess(T response);
}
