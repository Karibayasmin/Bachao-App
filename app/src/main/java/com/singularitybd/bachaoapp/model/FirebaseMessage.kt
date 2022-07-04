package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FirebaseMessage(
    @SerializedName("content_available") val contentAvailable: Boolean?,
    @SerializedName("sound") val sound: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?

): Serializable