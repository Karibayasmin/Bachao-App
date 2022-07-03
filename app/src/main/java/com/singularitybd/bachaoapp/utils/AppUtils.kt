package com.singularitybd.bachaoapp.utils

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

object AppUtils {
    fun showToast(context: Context, message: String, isLong: Boolean){
        Toast.makeText(context, message, if (isLong) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
    }


        fun hasNetworkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}