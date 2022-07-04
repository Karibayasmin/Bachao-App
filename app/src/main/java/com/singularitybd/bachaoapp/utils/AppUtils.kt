package com.singularitybd.bachaoapp.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.singularitybd.bachaoapp.activity.MainActivity
import com.singularitybd.bachaoapp.activity.NotificationActivity
import com.singularitybd.bachaoapp.preference.PreferenceUtil
import com.singularitybd.bachaoapp.R

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

    fun showNotification(
        context: Context,
        title : String,
        message: String,
        channelId: String,
        notificationId: Int
    ) {
        /* Create an in intent for desired activity **/

        val isUserLoggedIn = PreferenceUtil.getIsLoggedIn(context)

        val intent = Intent(context, if (isUserLoggedIn) NotificationActivity::class.java else MainActivity::class.java)

        // Set Extras
        intent.putExtra("notificationId", notificationId)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        notificationBuilder.color = 0x474E54

        notificationBuilder.setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(false)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                channelId,
                channelId,
                importance
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationBuilder.setChannelId(channelId)
            notificationManager.createNotificationChannel(notificationChannel)

        } else {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
            notificationBuilder.setVibrate(longArrayOf(0, 1000, 500, 1000))
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}