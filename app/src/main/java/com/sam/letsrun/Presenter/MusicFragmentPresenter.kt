package com.sam.letsrun.Presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.Music
import com.sam.letsrun.View.MusicFragmentView

class MusicFragmentPresenter {

    lateinit var mView: MusicFragmentView

    @SuppressLint("Recycle")
    fun loadLocalMusic(activity: Activity) {
        val musicList = ArrayList<Music>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = activity.contentResolver.query(uri, null, selection, null, sortOrder)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                musicList.add(Music(data, title, album, artist))
            }
            mView.loadMusicSuccess(musicList)
        } else {
            mView.loadMusicFailed()
        }
    }
}