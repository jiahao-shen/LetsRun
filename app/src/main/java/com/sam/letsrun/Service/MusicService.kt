package com.sam.letsrun.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MusicService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

    }
}
