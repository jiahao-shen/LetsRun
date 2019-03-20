package com.sam.letsrun.View

import com.sam.letsrun.Model.Music

interface MusicFragmentView {

    fun loadMusicSuccess(musicList: ArrayList<Music>)

    fun loadMusicFailed()

}