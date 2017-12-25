package com.apm29.network.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.apm29.network.ApiCall;

/**
 * Created by apm29 on 2017/9/28.
 */

public class NetworkUtils {
    private String android_id;

    /**
     * 检查有没有网络
     *
     * @param context
     * @return
     */
    public static boolean checkNetwork(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo=null;
            if (mConnectivityManager != null)
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }
    /**
     * UUID
     * @param
     * @return
     */
    public  String getAndroidId(){
        if (TextUtils.isEmpty(android_id)){
            if (!hasPermission(ApiCall.Companion.getApp(),Manifest.permission.READ_PHONE_STATE)){
                return "";
            }
        }
        android_id=getAndroidId(ApiCall.Companion.getApp());
        return  android_id ;
    }

    private boolean hasPermission(Context app, String permission) {
        int selfPermission = ContextCompat.checkSelfPermission(app, permission);
        return selfPermission== PackageManager.PERMISSION_GRANTED;
    }

    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }
}
