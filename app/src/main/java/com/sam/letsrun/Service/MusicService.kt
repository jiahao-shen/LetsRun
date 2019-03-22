package com.sam.letsrun.Service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.MusicController
import com.sam.letsrun.Model.MusicResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var timer = Timer()
    private var task: TimerTask? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        mediaPlayer = MediaPlayer()

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()

            task?.cancel()

            task = object: TimerTask() {
                override fun run() {
                    if (mediaPlayer.isPlaying) {
                        val percent = mediaPlayer.currentPosition * 1.0 / mediaPlayer.duration
                        EventBus.getDefault().postSticky(MusicResponse(Const.MUSIC_PERCENT, percent))
                    }
                }
            }
            timer.schedule(task, 0, 1000)
        }

        mediaPlayer.setOnCompletionListener {
            EventBus.getDefault().postSticky(MusicResponse(Const.MUSIC_COMPLETE, null))
        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessage(event: MusicController) {
        when (event.msg) {
            Const.MUSIC_START -> {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(event.uri)
                mediaPlayer.prepareAsync()
            }
            Const.MUSIC_PAUSE -> {
                mediaPlayer.pause()
            }
            Const.MUSIC_STOP -> {
                mediaPlayer.stop()
            }
            Const.MUSIC_RESUME -> {
                mediaPlayer.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mediaPlayer.release()
        timer.cancel()
        timer.purge()
    }
}
