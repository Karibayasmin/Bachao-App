package com.singularitybd.bachaoapp.network

import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.utils.EndPoints
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiHandler {
    //Post Registration
    @Multipart
    @POST(EndPoints.API_REGISTRATION)
    fun postCareerProfile(@Part("name") name : RequestBody, @Part("email")  email : RequestBody, @Part("mobile")  mobile : RequestBody, @Part("alternativeMobile")  alternativeMobile : RequestBody, @Part("institute")  institute : RequestBody, @Part("deptOrSubject")  deptOrSubject : RequestBody, @Part("yearOfPassing")  yearOfPassing : RequestBody, @Part("yearsOfJobExp")  yearsOfJobExp : RequestBody, @Part("interestedSector")  interestedSector : RequestBody, @Part("socialUrl")  socialUrl : RequestBody, @Part("expertise")  expertise : RequestBody): Call<CommonResponse>

}