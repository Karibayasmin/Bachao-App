package com.singularitybd.bachaoapp.network

import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.model.FcmSubmission
import com.singularitybd.bachaoapp.model.Help
import com.singularitybd.bachaoapp.model.LoginResponse
import com.singularitybd.bachaoapp.utils.EndPoints
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiHandler {
    //Post Registration
    @Multipart
    @POST(EndPoints.API_REGISTRATION)
    fun postRegistration(@Part("name") name : RequestBody, @Part("mobile")  mobile : RequestBody, @Part("password")  password : RequestBody, @Part("type")  type : RequestBody, @Part("is_volunteer")  is_volunteer : RequestBody, @Part("secret_word")  secret_word : RequestBody, @Part("email")  email : RequestBody, @Part("family_mobile")  family_mobile : RequestBody, @Part("previous_social_experience")  previous_social_experience : RequestBody, @Part("occupation")  occupation : RequestBody, @Part("lat_lon")  lat_lon : RequestBody, @Part("address")  address : RequestBody, @Part("blood_group")  blood_group : RequestBody): Call<CommonResponse>

    //Post Login
    @Multipart
    @POST(EndPoints.API_LOGIN)
    fun postLogin(@Part("mobile")  mobile : RequestBody, @Part("password")  password : RequestBody): Call<LoginResponse>

    // Submit FCM
    @POST(EndPoints.API_POST_FCM)
    fun submitFcm(@Body fcmSubmission: FcmSubmission): Call<CommonResponse>?

    // Submit FCM
    @POST(EndPoints.API_POST_HELP)
    fun submitHelpRequest(@Body help: Help): Call<CommonResponse>?

}