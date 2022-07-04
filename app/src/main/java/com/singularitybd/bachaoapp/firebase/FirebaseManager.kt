package com.singularitybd.bachaoapp.firebase

import android.app.Notification
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.singularitybd.bachaoapp.model.FirebaseMessage
import com.singularitybd.bachaoapp.preference.PreferenceUtil
import com.singularitybd.bachaoapp.utils.AppConstants
import kotlin.random.Random
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.utils.AppUtils

class FirebaseManager : FirebaseMessagingService(){

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)

//        val title : String = remoteMessage.notification?.title ?: getString(R.string.app_name)
//        val message : String = remoteMessage.notification?.body ?: getString(R.string.common_pushNotification_body)

            val channelId : String = remoteMessage.notification?.channelId ?: AppConstants.DEFAULT_CHANNEL_ID

            val data = remoteMessage.data

            val messageBody : FirebaseMessage =
                GsonBuilder().create().fromJson(data["message"], FirebaseMessage::class.java)
                    ?: FirebaseMessage(
                        false,
                        "",
                        getString(R.string.app_name),
                        getString(R.string.common_pushNotification_body)
                    )

            val titleData: String = messageBody.title ?: getString(R.string.app_name)
            val messageData: String = messageBody.body ?: getString(R.string.common_pushNotification_body)

            AppUtils.showNotification(
                this,
                titleData,
                messageData,
                channelId,
                Random.nextInt()
            )

            /*val notification = Notification()
            notification.title = titleData
            notification.message = messageData*/

            //PreferenceUtil.saveNotification(this, notification)

        }

        override fun onNewToken(token: String) {
            super.onNewToken(token)
            Log.e("token", "$token")
            PreferenceUtil.saveFcmToken(this, token)

            FirebaseHandler.registerFcmTokenServer(this)
        }
}