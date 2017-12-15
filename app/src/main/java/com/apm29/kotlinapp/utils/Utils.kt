package com.apm29.kotlinapp.utils

import android.annotation.SuppressLint
import android.os.SystemClock
import android.support.annotation.StringRes
import android.widget.Toast
import com.apm29.kotlinapp.MyApp

/**
 * top level函数
 * Created by dingzhu on 2017/12/15.
 */
class Utils{
    companion object {
         val toast:Toast by lazy {
             return@lazy  Toast.makeText(MyApp.getApplication(), "",Toast.LENGTH_SHORT)
         }
    }
    var property="123"
    fun test(): Unit {
        val apply = Utils().apply {
            property = "sss"
        }
        Utils().let {
            it.property.apply {
                property=SystemClock.currentThreadTimeMillis().toString()+"s"
            }
        }
    }
}

fun toPx(dp: Int): Int =
        (MyApp.getApplication().resources.displayMetrics.density * dp).toInt()

fun getWindowWidth(): Int =
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