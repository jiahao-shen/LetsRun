package com.sam.letsrun.Service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import com.orhanobut.logger.Logger
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.EventMusicList
import com.sam.letsrun.Model.Music
import com.sam.letsrun.Model.MusicController
import com.sam.letsrun.Model.MusicResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var musicList = ArrayList<Music>()
    private var currentIndex = -1

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun loadLocalMusic() {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                musicList.add(Music(data, title, album, artist))
            }
            cursor.close()
            EventBus.getDefault().postSticky(EventMusicList(musicList))
        } else {
            EventBus.getDefault().postSticky(EventMusicList(musicList))
        }
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        loadLocalMusic()

        mediaPlayer = MediaPlayer()

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }

        mediaPlayer.setOnCompletionListener {
            EventBus.getDefault().postSticky(MusicResponse(Const.MUSIC_COMPLETE))
        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessage(event: MusicController) {
        when (event.msg) {
            Const.MUSIC_START -> {
                currentIndex = event.index!!
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(musicList[currentIndex].data)
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
    }
}
