package com.apm29.beanmodule.beans.ding

data class BaseResponse<out T>(val result: T,
                               val code: String = "",
                               val success: Boolean = false,
                               val errorMsg: String = "")