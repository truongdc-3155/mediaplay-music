package com.truongdc21.mediaplaymusic.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.truongdc21.mediaplaymusic.Service.MusicService
import com.truongdc21.mediaplaymusic.Unit.Contans

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intentReceiver: Intent?) {
        val actionMusic =  intentReceiver?.getStringExtra(Contans.ACTION_MUSIC)
        Intent(context, MusicService::class.java).also {
            it.action = actionMusic
            context?.startService(it)
        }
    }
}
