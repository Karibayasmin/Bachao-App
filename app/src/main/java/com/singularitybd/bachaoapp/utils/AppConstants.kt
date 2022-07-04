package com.singularitybd.bachaoapp.utils

object AppConstants {
    /*For network*/
    const val BASE_URL = "http://104.248.159.155/api/v1/"

    /*For shared preference*/
    const val SHARED_TABLE_NAME = "SHOHAY"
    const val SHARED_USER_SECRET_WORD = "SECRET_WORD"
    const val SHARED_ACCESS_TOKEN = "ACCESS_TOKEN"
    const val SHARED_IS_LOGGED_IN = "IS_LOGGED_IN"
    const val SHARED_USER_ID = "SHARED_USER_ID"

    /*MainActivity*/
    const val REQUEST_AUDIO_PERMISSION_CODE = 1
    const val REQUEST_CODE_SPEECH_INPUT = 2

    /*Firebase Push Notification*/

    const val FCM_TOKEN = "FCM_TOKEN"
    const val IS_FCM_TOKEN_SENT = "IS_FCM_TOKEN_SENT"
    const val DEVICE_TYPE = "android"

    const val DEFAULT_CHANNEL_ID = "com.singularitybd.bachaoapp.default"

    /*BaseActivity*/
    const val LOCATION_PERMISSION_CODE = 3

}