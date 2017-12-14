package com.apm29.kotlinapp.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by dingzhu on 2017/12/14.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("J_PUSH"+"context = [" + context + "], intent = [" + intent + "]");
    }
}
