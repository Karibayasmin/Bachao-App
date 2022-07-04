package com.singularitybd.bachaoapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.model.Help
import com.singularitybd.bachaoapp.network.ApiClient
import com.singularitybd.bachaoapp.network.ApiHandler
import com.singularitybd.bachaoapp.preference.PreferenceUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainActivityViewModel : ViewModel() {

    var mutableHelpSeekViewModel = MutableLiveData<CommonResponse>()

    fun sentHelpRequest(context: Context, latLon : String, id : Int): LiveData<CommonResponse>{
        mutableHelpSeekViewModel = MutableLiveData()
        postHelpRequest(context, latLon, id)
        return mutableHelpSeekViewModel
    }

    private fun postHelpRequest(context: Context, latLon: String, id: Int) {
        var call = ApiClient?.getInstance(PreferenceUtil.getAccessToken(context))?.create(ApiHandler::class.java)?.submitHelpRequest(
            Help(latLon, id))

        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if(response.isSuccessful && response.body() != null){
                    response.body()?.let { setHelpResponse(it) }
                }else{
                    if(response.errorBody() == null){
                        setHelpResponse(CommonResponse(response.code(), response.message()))
                    }else{
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            setHelpResponse(CommonResponse(response.code(), jObjError.getString("app_message") ?: response.message()))
                        }catch (e: Exception){
                            setHelpResponse(CommonResponse(response.code(), e.message.toString()))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                setHelpResponse(CommonResponse(100, t.message))
            }

        })
    }

    private fun setHelpResponse(data: CommonResponse) {
        mutableHelpSeekViewModel.value = data
    }
}