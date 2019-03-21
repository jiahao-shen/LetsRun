package com.sam.letsrun.Adapter

import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.Music
import com.sam.letsrun.R


class MusicAdapter(data: ArrayList<Music>) : BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music_list, data) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun convert(helper: BaseViewHolder, item: Music) {
        helper.setText(R.id.musicTitleText, item.title)
                .setText(R.id.musicArtistText, item.artist + " • ${item.album}")

        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(item.data)
        val data = mediaMetadataRetriever.embeddedPicture

        if (data != null) {
            helper.setImageBitmap(R.id.musicAlbumArtView, ImageUtils.bytes2Bitmap(data))
        } else {
            helper.setImageResource(R.id.musicAlbumArtView, R.drawable.ic_music_album_art)
        }

        if (item.selected) {
            helper.setTextColor(R.id.musicTitleText, Color.parseColor("#6ccd82"))
                    .setTextColor(R.id.musicArtistText, Color.parseColor("#6ccd82"))
                    .setBackgroundColor(R.id.musicSelectedView, Color.parseColor("#6ccd82"))


        } else {
            helper.setTextColor(R.id.musicTitleText, Color.BLACK)
                    .setTextColor(R.id.musicArtistText, Color.GRAY)
                    .setBackgroundColor(R.id.musicSelectedView, Color.TRANSPARENT)
        }

    }

}
