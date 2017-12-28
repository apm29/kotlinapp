package com.apm29.kotlinapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.StringRes
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.apm29.kotlinapp.MyApp
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

/**
 * top level函数
 * Created by dingzhu on 2017/12/15.
 */
class Utils private constructor() {
    companion object {
        val toast: Toast by lazy {
            return@lazy Toast.makeText(getApp(), "", Toast.LENGTH_SHORT)
        }
        val sp:SharedPreferences by lazy {
            return@lazy getApp().getSharedPreferences("DingDing",Context.MODE_PRIVATE)
        }
    }
}

/**
 * topLevel functions
 */
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


/**
 * SharePreferenceUtils
 */
fun getInt(key: String): Int {
    return getInt(key,0)
}
fun getInt(key:String,def:Int): Int {
    return Utils.sp.getInt(key,def)
}
@SuppressLint("ApplySharedPref")
fun putInt(key: String, value: Int) {
    Utils.sp.edit().putInt(key,value).commit()
}

fun getString(key: String): String {
    return getString(key,"")
}
fun getString(key:String,def:String): String {
    return Utils.sp.getString(key,def)
}
@SuppressLint("ApplySharedPref")
fun putString(key: String, value: String) {
    Utils.sp.edit().putString(key,value).commit()
}

fun getBoolean(key: String): Boolean {
    return getBoolean(key,false)
}

fun getBoolean(key:String,def:Boolean): Boolean {
    return Utils.sp.getBoolean(key,def)
}

@SuppressLint("ApplySharedPref")
fun putBoolean(key: String, value: Boolean) {
    Utils.sp.edit().putBoolean(key,value).commit()
}


/**
 * MD5
 */
private val mHexStr = "0123456789ABCDEF"
fun md5(value: String): String {
    try {
        val md = MessageDigest.getInstance("MD5")
        return bytesToHexString(md.digest(value.toByteArray(charset("UTF-8"))))
    } catch (e: Exception) {
        e.printStackTrace()
        return value
    }

}

/**
 * @param bytes
 * @return 将二进制转换为十六进制字符输出
 */
private fun bytesToHexString(bytes: ByteArray): String {
    val result = StringBuilder()
    val count = bytes.size
    for (i in 0 until count) {
        i shr 4
        // 字节高4位
        val int: Int = ((bytes[i] and 0xF0.toByte()).toInt() shr 4)
        result.append(mHexStr[int].toString())
        // 字节低4位
        result.append(mHexStr[(bytes[i] and 0x0F) .toInt()].toString())
    }
    return result.toString()
}

