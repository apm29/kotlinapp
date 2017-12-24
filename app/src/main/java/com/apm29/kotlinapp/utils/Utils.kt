package com.apm29.kotlinapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.StringRes
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.apm29.kotlinapp.MyApp
import java.util.*

/**
 * top level函数
 * Created by dingzhu on 2017/12/15.
 */
class Utils private constructor() {
    companion object {
        val toast: Toast by lazy {
            return@lazy Toast.makeText(getApp(), "", Toast.LENGTH_SHORT)
        }
    }
}

val tm = getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

fun clamp(value: Int, min: Int, max: Int): Int {
    return if (value < min) min else if (value > max) max else value
}

fun toPx(dp: Int): Int =
        (getApp().resources.displayMetrics.density * dp).toInt()

fun Int.px(): Int =
        (getApp().resources.displayMetrics.density * this).toInt()

fun getWindowWidth(): Int =
        getApp().resources.displayMetrics.widthPixels

fun getWindowHeight(): Int =
        getApp().resources.displayMetrics.heightPixels

fun getApp() = MyApp.getApplication()

@SuppressLint("ShowToast")
fun showToast(text: CharSequence) {
    Utils.toast.setText(text)
    Utils.toast.show()
}

@SuppressLint("ShowToast")
fun showToast(@StringRes res: Int) {
    Utils.toast.setText(res)
    Utils.toast.show()
}

const val TAG_PREFIX = "<<KotlinApp>> -- "

/**log*/
fun Any.log(msg: String?) {
    logD(TAG_PREFIX + this::class.java.simpleName, msg)
}

fun Any.logD(msg: String?) {
    logD(TAG_PREFIX + this::class.java.simpleName, msg)
}

fun Any.logD(tag: String, msg: String?) {
    Log.d(tag, msg)
}

fun Any.logI(msg: String?) {
    logI(TAG_PREFIX + this::class.java.simpleName, msg)
}

fun Any.logI(tag: String, msg: String?) {
    Log.i(tag, msg)
}

@SuppressLint("MissingPermission", "HardwareIds")
fun uuid(context: Context): String {
    var tmDevice: String = ""
    var tmSerial: String = ""
    var androidId: String = ""
    try {
        tmDevice = "" + tm.getDeviceId()
        tmSerial = "" + tm.getSimSerialNumber()
        androidId = "" + android.provider.Settings.Secure.getString(context.applicationContext.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val deviceUuid = UUID(androidId.hashCode().toLong(), tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())
    return deviceUuid.toString()
}

