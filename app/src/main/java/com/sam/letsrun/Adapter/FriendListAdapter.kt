package com.sam.letsrun.Adapter

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.Friend
import com.sam.letsrun.R

/**
 * 好友列表适配器
 */
class FriendListAdapter(data: ArrayList<Friend>) : BaseQuickAdapter<Friend, BaseViewHolder>(R.layout.item_friend_list, data) {

    override fun convert(helper: BaseViewHolder, item: Friend) {
        helper.setText(R.id.userNameText, item.username)
                .setText(R.id.userSignatureText, item.signature)

        if (item.device == null) {
            helper.setText(R.id.userDeviceText, "[离线]")
        } else {
            helper.setText(R.id.userDeviceText, "[${item.device}在线]")
        }

        GlideApp.with(mContext)
                .load(MyUtils.getImageUrl(item.telephone))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_user_image)
                .into(helper.getView(R.id.userImageView))
    }
}