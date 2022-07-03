package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class EventSpeechRecognise (
    @SerializedName("isSpeechRecognised")
    var isSpeechRecognised : Boolean? = false,

    @SerializedName("isSpeechRecognised")
    var recognisedWord : String? = "",

): Serializable