package com.sam.letsrun.View

import android.graphics.Bitmap

/**
 * Created by sam on 2018/3/13.
 */
interface RegisterView {

    fun nextFragment()

    fun selectImageAlbum()

    fun selectImageCamera()

    fun initTelephone(data: String)

    fun initPassword(data: String)

    fun initUserName(data: String)

    fun initGender(data: String)

    fun initBirthday(data: String)

    fun initBlood(data: String)

    fun initHeight(data: Int)

    fun initWeight(data: Int)

    fun unKnownError()

    fun registerSuccess(token: String)

    fun netError()

}