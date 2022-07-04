package com.singularitybd.bachaoapp.preference

import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.singularitybd.bachaoapp.model.ProfileData
import com.singularitybd.bachaoapp.utils.AppConstants

object PreferenceUtil {
    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(AppConstants.SHARED_TABLE_NAME, Context.MODE_PRIVATE)
    }

    fun removeAllUserData(context: Context) {
        val editor = getSharedPreference(context).edit()
        editor.clear()
        editor.apply()
    }

    fun getIsLoggedIn(context: Context) : Boolean {
        val preferences = getSharedPreference(context)
        return preferences.getBoolean(AppConstants.SHARED_IS_LOGGED_IN, false)
    }

    fun saveIsLoggedIn(context: Context, isLoggedIn: Boolean) {
        val editor = getSharedPreference(context).edit()
        editor.putBoolean(AppConstants.SHARED_IS_LOGGED_IN, isLoggedIn ?: false)
        editor.apply()
    }

    fun getAccessToken(context: Context) : String {
        val preferences = getSharedPreference(context)
        return preferences.getString(AppConstants.SHARED_ACCESS_TOKEN, "") ?: ""
    }

    fun saveAccessToken(context: Context, accessToken: String) {
        val editor = getSharedPreference(context).edit()
        editor.putString(AppConstants.SHARED_ACCESS_TOKEN, accessToken ?: "")
        editor.apply()
    }

    fun getUserSecretWord(context: Context) : String {
        val preferences = getSharedPreference(context)
        return preferences.getString(AppConstants.SHARED_USER_SECRET_WORD, "") ?: ""
    }

    fun getUserId(context: Context) : Int {
        val preferences = getSharedPreference(context)
        return preferences.getInt(AppConstants.SHARED_USER_ID, 0)
    }

    fun saveUserData(context: Context, userProfile: ProfileData) {
        val editor = getSharedPreference(context).edit()
        editor.putString(AppConstants.SHARED_USER_SECRET_WORD, userProfile.secretWord ?: "")
        editor.putInt(AppConstants.SHARED_USER_ID, userProfile.id ?: 0)
        editor.apply()
    }

    fun getIsTokenSubmitted(context: Context) : Boolean {
        val preference = getSharedPreference(context)

        return preference.getBoolean(AppConstants.IS_FCM_TOKEN_SENT, false)
    }

    fun saveIsTokenSubmitted(context: Context, isSubmitted: Boolean) {
        val editor = getSharedPreference(context).edit()
        editor.putBoolean(AppConstants.IS_FCM_TOKEN_SENT, isSubmitted)
        editor.apply()
    }

    fun saveFcmToken(context: Context, token: String) {
        if (isSimilarFcmToken(context, token)) return

        val editor = getSharedPreference(context).edit()
        editor.putString(AppConstants.FCM_TOKEN, token)
        editor.putBoolean(AppConstants.IS_FCM_TOKEN_SENT, false)
        editor.apply()
    }

    fun isSimilarFcmToken(context: Context, newToken: String): Boolean {
        return getFcmToken(context) == newToken
    }

    fun getFcmToken(context: Context) : String {
        val preference = getSharedPreference(context)

        return preference.getString(AppConstants.FCM_TOKEN, "") ?: ""
    }
}