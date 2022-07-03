package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GpsEvent (
    @SerializedName("isGpsUpdated")
    var isGpsUpdated : Boolean? = false,

    @SerializedName("latLon")
    var latLon : String? = "",

    @SerializedName("address")
    var address : String? = "",

    ): Serializable