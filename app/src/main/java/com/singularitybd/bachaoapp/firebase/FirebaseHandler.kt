package com.singularitybd.bachaoapp.firebase

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.model.FcmSubmission
import com.singularitybd.bachaoapp.network.ApiClient
import com.singularitybd.bachaoapp.network.ApiHandler
import com.singularitybd.bachaoapp.preference.PreferenceUtil
import com.singularitybd.bachaoapp.utils.AppConstants
import com.singularitybd.bachaoapp.utils.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FirebaseHandler {

    /*Firebase auto initialization and enable*/

    fun enableFirebase(){
        if(!FirebaseMessaging.getInstance().isAutoInitEnabled){
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        }
    }

    fun disableFirebase(){
        FirebaseMessaging.getInstance().isAutoInitEnabled = false

        Thread{
            try {
                FirebaseMessaging.getInstance().deleteToken()

            } catch (e : Exception){
                e.stackTrace

            }
        }.start()
    }

    fun registerFcmTokenServer(context: Context){
        val preferences = context.getSharedPreferences(AppConstants.SHARED_TABLE_NAME, Context.MODE_PRIVATE)

        if(!AppUtils.hasNetworkConnection(context)
            || preferences.getString(AppConstants.FCM_TOKEN,"").equals("")
            || !preferences.getBoolean(AppConstants.SHARED_IS_LOGGED_IN, false)
            || PreferenceUtil.getIsTokenSubmitted(context)
        ){
            return
        }

        val fcmSubmission = FcmSubmission(preferences.getString(AppConstants.FCM_TOKEN, "").toString())

        val call = ApiClient
            .getInstance(PreferenceUtil.getAccessToken(context))
            ?.create(ApiHandler::class.java)?.submitFcm(fcmSubmission)

        call?.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful && response.body() != null && response.body()?.responseCode == 200) {
                    PreferenceUtil.saveIsTokenSubmitted(context, true)
                }
            }
        })

    }
}