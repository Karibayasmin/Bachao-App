package com.singularitybd.bachaoapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.network.ApiClient
import com.singularitybd.bachaoapp.network.ApiHandler
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RegistrationViewModel : ViewModel() {

    var mutableRegistrationResponse = MutableLiveData<CommonResponse>()

    fun submitRegistration(
        context: Context,
        name: RequestBody,
        mobileNumber: RequestBody,
        userType: RequestBody,
        isVolunteer: RequestBody,
        secretWord: RequestBody,
        email: RequestBody,
        emergencyNumber: RequestBody,
        previousExperience: RequestBody,
        occupation: RequestBody,
        latitudeLongitude: RequestBody,
        address: RequestBody,
        bloodGroup: RequestBody,
        password: RequestBody
    ): LiveData<CommonResponse>{
        mutableRegistrationResponse = MutableLiveData()
        postRegistrationResponse(context, name, mobileNumber, userType, isVolunteer, secretWord, email, emergencyNumber, previousExperience, occupation, latitudeLongitude, address, bloodGroup, password)
        return mutableRegistrationResponse
    }

    private fun postRegistrationResponse(
        context: Context,
        name: RequestBody,
        mobileNumber: RequestBody,
        userType: RequestBody,
        isVolunteer: RequestBody,
        secretWord: RequestBody,
        email: RequestBody,
        emergencyNumber: RequestBody,
        previousExperience: RequestBody,
        occupation: RequestBody,
        latitudeLongitude: RequestBody,
        address: RequestBody,
        bloodGroup: RequestBody,
        password: RequestBody
    ) {
        var call = ApiClient?.getInstance("")?.create(ApiHandler::class.java)?.postRegistration(name, mobileNumber, password, userType, isVolunteer, secretWord, email, emergencyNumber, previousExperience, occupation, latitudeLongitude, address, bloodGroup)

        call?.enqueue(object : Callback<CommonResponse>{
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if(response.isSuccessful && response.body() != null){
                    response.body()?.let { setRegistrationResponse(it) }
                }else{
                    if(response.errorBody() == null){
                        setRegistrationResponse(CommonResponse(response.code(), response.message()))
                    }else{
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            setRegistrationResponse(CommonResponse(response.code(), jObjError.getString("app_message") ?: response.message()))
                        }catch (e: Exception){
                            setRegistrationResponse(CommonResponse(response.code(), e.message.toString()))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                setRegistrationResponse(CommonResponse(100, t.message))
            }

        })
    }

    private fun setRegistrationResponse(data: CommonResponse) {
        mutableRegistrationResponse.value = data
    }
}