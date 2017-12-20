package com.apm29.kotlinapp.utils

import android.annotation.SuppressLint
import android.support.annotation.StringRes
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
             return@lazy  Toast.makeText(MyApp.getApplication(), "",Toast.LENGTH_SHORT)
         }
    }
}

fun toPx(dp: Int): Int =
        (MyApp.getApplication().resources.displayMetrics.density * dp).toInt()

fun Int.px():Int =
        (MyApp.getApplication().resources.displayMetrics.density * this).toInt()

fun BaseActivity<*>.getWindowWidth(): Int =
        MyApp.getApplication().resources.displayMetrics.widthPixels

fun getWindowHeight():Int =
        MyApp.getApplication().resources.displayMetrics.heightPixels

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

fun <T:BasePresenter> BaseActivity<T>.showToast(msg: String) {
    Utils.toast.setText(msg)
    Utils.toast.show()
}