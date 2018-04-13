package com.sam.letsrun.Common

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * 工具类
 */
object MyUtils {

    /**
     * 生成md5码
     * @param text(原字符串)
     */
    fun md5(text: String): String {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(text.toByteArray())
        val sb = StringBuffer()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) hexString = "0$hexString"
            sb.append(hexString)
        }
        return sb.toString()
    }

    /**
     * 图片压缩并保存
     * @param bitmap(图片)
     * @param name(图片名称
     */
    fun saveImageView(bitmap: Bitmap, name: String) {
        val file = File(Const.LOCAL_PATH, "$name.jpg")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

    }

    fun getImageUrl(name: String) = "${Const.BASE_HTTP_ADDRESS}/UserImage/$name.jpg"

}