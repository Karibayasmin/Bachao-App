package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CommonResponse (
    @SerializedName("code")
    var responseCode : Int? = 0,

    @SerializedName("app_message")
    var appMessage : String? = "",

    @SerializedName("user_message")
    var userMessage : String? = ""

): Serializable