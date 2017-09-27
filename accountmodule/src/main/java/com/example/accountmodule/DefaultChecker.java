package com.example.accountmodule;

import android.widget.TextView;

import com.example.accountmodule.base.IChecker;

/**
 * Created by yingjiawei on 2017/9/26.
 */

public class DefaultChecker implements IChecker<Boolean,TextView> {
    @Override
    public Boolean check(TextView param) {
        return false;
    }
}
