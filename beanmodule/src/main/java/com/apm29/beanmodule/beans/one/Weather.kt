package com.apm29.beanmodule.beans.one

import com.google.gson.annotations.SerializedName

data class Weather(val date: String = "",
                   @SerializedName("city_name")
                   val cityName: String = "",
                   val temperature: String = "",
                   val humidity: String = "",
                   @SerializedName("wind_direction")
                   val windDirection: String = "",
                   val hurricane: String = "",
                   val climate: String = "",
                   val icons: Icons?)