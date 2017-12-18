package com.apm29.beanmodule.Init


/**
 * Created by apm29 on 2017/9/8.
 */
class Response<DATA>{
    /**
     * meta
     */
    var meta:Meta?=null
    /**
     * data
     */
    var data:DATA?=null


    data class Meta(
        var count:Int?=null,
        var code:Int?=null,
        var desc:String?=null
    )

}