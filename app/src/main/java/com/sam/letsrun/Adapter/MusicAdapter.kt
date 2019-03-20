package com.sam.letsrun.Adapter

import android.media.MediaMetadataRetriever
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.orhanobut.logger.Logger
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.Music
import com.sam.letsrun.R


class MusicAdapter(data: ArrayList<Music>) : BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music_list, data) {

    override fun convert(helper: BaseViewHolder, item: Music) {
        helper.setText(R.id.musicTitleText, item.title)
                .setText(R.id.musicArtistText, item.artist + " • ${item.album}")

        val mediaMetadataRetriever = MediaMetadataRetriever()
        Logger.e(item.data)
        mediaMetadataRetriever.setDataSource(item.data)
        val data = mediaMetadataRetriever.embeddedPicture

        data?.let {
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(data)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_music)
                    .into(helper.getView(R.id.musicAlbumArtView))
        }

    }
}
