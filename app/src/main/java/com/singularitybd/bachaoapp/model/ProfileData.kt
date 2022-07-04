package com.singularitybd.bachaoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProfileData (
    @SerializedName("id")
    var id : Int? = 0,

    @SerializedName("name")
    var name : String? = "",

    @SerializedName("mobile")
    var mobile : String? = "",

    @SerializedName("image_url")
    var imageUrl : String? = "",

    @SerializedName("type")
    var type : String? = "",

    @SerializedName("is_volunteer")
    var isVolunteer : String? = "",

    @SerializedName("secret_word")
    var secretWord : String? = "",

    @SerializedName("email")
    var email : String? = "",

    @SerializedName("family_mobile")
    var emergencyMobile : String? = "",

    @SerializedName("previous_social_experience")
    var previousSocialExperience : String? = "",

    @SerializedName("occupation")
    var occupation : String? = "",

    @SerializedName("address")
    var address : String? = "",

    @SerializedName("blood_group")
    var bloodGroup : String? = "",

) : Serializable