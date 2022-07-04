package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FcmSubmission(
    @SerializedName("device_token") val token: String,

) : Serializable