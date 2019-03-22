package com.sam.letsrun.View

import com.sam.letsrun.Model.Music

interface MusicFragmentView {

    fun loadLocalMusicSuccess(musics: ArrayList<Music>)

    fun loadLocalMusicFailed()

}