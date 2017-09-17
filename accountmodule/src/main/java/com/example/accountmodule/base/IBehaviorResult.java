package com.example.accountmodule.base;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public interface IBehaviorResult<T> {
    void onFailure(AccountError error);
    void onSuccess(T response);
}
