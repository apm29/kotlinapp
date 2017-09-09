package com.apm29.kotlinapp.base;

/**
 * Created by apm29 on 2017/9/7.
 */

public interface BaseUI {
    void startLoading();
    void stopLoading();
    void onError(String error);
    <N> void onNewData(N data);
    void onEmpty();
}
