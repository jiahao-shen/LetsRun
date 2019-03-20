package com.sam.letsrun.View

import android.graphics.Bitmap

/**
 * Created by sam on 2018/3/13.
 */
interface RegisterView {

    fun unKnownError()

    fun registerSuccess(token: String)

    fun netError()

}