package com.singularitybd.bachaoapp.preference

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

    fun getUserSecretWord(context: Context) : String {
        val preferences = getSharedPreference(context)
        return preferences.getString(AppConstants.SHARED_USER_SECRET_WORD, "") ?: ""
    }

    fun saveUserData(context: Context, userProfile: ProfileData) {
        val editor = getSharedPreference(context).edit()
        editor.putString(AppConstants.SHARED_USER_SECRET_WORD, userProfile.secretWord ?: "")
        editor.apply()
    }
}