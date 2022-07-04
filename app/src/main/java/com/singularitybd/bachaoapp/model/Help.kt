package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Help (
    @SerializedName("lat_lon") val latLon: String,
    @SerializedName("user_id") val userId: Int,

) : Serializable