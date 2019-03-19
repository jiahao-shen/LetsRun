package com.sam.letsrun.Custom

import android.graphics.Bitmap
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * 工具类
 */
object MyUtils {

    fun getImageUrl(name: String) = "${Const.BASE_HTTP_ADDRESS}UserImage/$name.jpg"

}