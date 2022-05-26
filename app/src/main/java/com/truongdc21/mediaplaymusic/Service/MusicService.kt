package com.truongdc21.mediaplaymusic.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MusicService () : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}

