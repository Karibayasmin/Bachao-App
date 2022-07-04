package com.singularitybd.bachaoapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.model.LoginResponse
import com.singularitybd.bachaoapp.network.ApiClient
import com.singularitybd.bachaoapp.network.ApiHandler
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LoginViewModel : ViewModel() {

    var mutableLoginResponse = MutableLiveData<LoginResponse>()

    fun submitLogin(context: Context, mobileNumber: RequestBody, password: RequestBody) : LiveData<LoginResponse>{
        mutableLoginResponse = MutableLiveData()
        postLogin(context, mobileNumber, password)
        return mutableLoginResponse
    }

    private fun postLogin(
        context: Context,
        mobileNumber: RequestBody,
        password: RequestBody
    ) {

        var call = ApiClient?.getInstance("")?.create(ApiHandler::class.java)?.postLogin(mobileNumber, password)

        call?.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if(response.isSuccessful && response.body() != null){
                    response.body()?.let { setLoginResponse(it) }
                }else{
                    if(response.errorBody() == null){
                        setLoginResponse(LoginResponse(response.code(), response.message()))
                    }else{
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            setLoginResponse(LoginResponse(response.code(), jObjError.getString("app_message") ?: response.message()))
                        }catch (e: Exception){
                            setLoginResponse(LoginResponse(response.code(), e.message.toString()))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setLoginResponse(LoginResponse(100, t.message))
            }

        })

    }

    private fun setLoginResponse(data: LoginResponse) {
        mutableLoginResponse.value = data
    }
}