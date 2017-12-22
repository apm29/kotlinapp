package com.apm29.beanmodule.beans.one

import com.google.gson.annotations.SerializedName

data class Data(val date: String = "",
                @SerializedName("content_list")
                val contentList: List<ContentListItem>?,
                val weather: Weather?,
                val id: String = "")