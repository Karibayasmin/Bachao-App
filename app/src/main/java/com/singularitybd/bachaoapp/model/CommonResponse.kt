package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CommonResponse (
    @SerializedName("response_code")
    var responseCode : Int? = 0,

    @SerializedName("status")
    var status : String? = "",

    @SerializedName("message")
    var message : String? = ""

): Serializable