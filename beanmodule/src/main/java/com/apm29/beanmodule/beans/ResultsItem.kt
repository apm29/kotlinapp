package com.apm29.beanmodule.beans

import com.google.gson.annotations.SerializedName

data class ResultsItem(@SerializedName("updated_at")
                       val updatedAt: String = "",
                       val publishedAt: String = "",
                       @SerializedName("created_at")
                       val createdAt: String = "",
                       @SerializedName("_id")
                       val Id: String = "",
                       @SerializedName("rand_id")
                       val randId: String = "",
                       val title: String = "",
                       val content: String = "")