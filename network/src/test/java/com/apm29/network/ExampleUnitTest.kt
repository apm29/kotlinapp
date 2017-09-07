package com.apm29.network

import io.reactivex.Observable
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        var arr1= arrayListOf<Int>(1,3,5,7,9,2,4,6,8)
        var arr2= arrayListOf<Int>(11,22,33,44,55,66,77,88,99)
        Observable.fromIterable(arr1)
                .filter {
                    it>3
                }
                .map {
                    it+3
                }
                .flatMap {
                    Observable.just(it.toString()+"\tString")
                }
                .subscribe {
                    println(it)
                }

    }
}