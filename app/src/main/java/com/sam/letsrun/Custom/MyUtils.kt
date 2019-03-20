package com.sam.letsrun.Custom

import android.graphics.Bitmap
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.*

/**
 * 工具类
 */
object MyUtils {

    fun getImageUrl(name: String) = Const.IMAGE_ADDRESS + "$name.jpg"

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)
        return "$year-$month-$date"
    }

}