package com.apm29.kotlinapp.ui

import android.content.ContentValues.TAG
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by apm29 on 2017/9/5.
 */

class Test {
    fun foo() {
        //创建一个上游 Observable：
        val observable = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onComplete()
        })
        //创建一个下游 Observer
        val observer = object : Observer<Int> {
            override fun onNext(t: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "subscribe")
            }
            override fun onError(e: Throwable) {
                Log.d(TAG, "error")
            }

            override fun onComplete() {
                Log.d(TAG, "complete")
            }
        }
        //建立连接
        observable.subscribe(observer)
    }
}
