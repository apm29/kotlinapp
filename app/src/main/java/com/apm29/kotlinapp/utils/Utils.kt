package com.apm29.kotlinapp.utils

import android.annotation.SuppressLint
import android.support.annotation.StringRes
import android.util.Log
import android.widget.Toast
import com.apm29.kotlinapp.MyApp
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter

/**
 * top level函数
 * Created by dingzhu on 2017/12/15.
 */
class Utils private constructor(){
    companion object {
         val toast:Toast by lazy {
             return@lazy  Toast.makeText(getApp(), "",Toast.LENGTH_SHORT)
         }
    }
}

fun toPx(dp: Int): Int =
        (getApp().resources.displayMetrics.density * dp).toInt()

fun Int.px():Int =
        (getApp().resources.displayMetrics.density * this).toInt()

fun getWindowWidth(): Int =
        getApp().resources.displayMetrics.widthPixels

fun getWindowHeight():Int =
        getApp().resources.displayMetrics.heightPixels

fun getApp() = MyApp.getApplication()

@SuppressLint("ShowToast")
fun showToast(text: CharSequence) {
        Utils.toast.setText(text)
        Utils.toast.show()
}
@SuppressLint("ShowToast")
fun showToast(@StringRes  res: Int) {
    Utils.toast.setText(res)
    Utils.toast.show()
}

const val TAG_PREFIX = "<<KotlinApp>> -- "

/**log*/
fun Any.log(msg: String?){
    logD(TAG_PREFIX +this::class.java.simpleName,msg)
}
fun Any.logD(msg: String?){
    logD(TAG_PREFIX +this::class.java.simpleName,msg)
}
fun Any.logD(tag:String,msg: String?){
    Log.d(tag,msg)
}
fun Any.logI(msg: String?){
    logI(TAG_PREFIX +this::class.java.simpleName,msg)
}
fun Any.logI(tag:String,msg: String?){
    Log.i(tag,msg)
}
