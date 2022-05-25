package com.truongdc21.mediaplaymusic

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication : Application() {
    companion object {
        const val CHANEL_ID = "playmusic_service"
    }
    override fun onCreate() {
        super.onCreate()

            createChanelNotification()
    }

    private fun createChanelNotification() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val chanel = NotificationChannel(CHANEL_ID , "Chanel Play Music",
                NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chanel)
        }
    }
}
