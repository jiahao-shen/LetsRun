package com.sam.letsrun.Adapter

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.AddFriendRequest
import com.sam.letsrun.R

/**
 * 添加好友适配器
 */
class AddFriendAdapter(data: ArrayList<AddFriendRequest>) :
        BaseQuickAdapter<AddFriendRequest, BaseViewHolder>(R.layout.item_add_friend_request, data) {

    override fun convert(helper: BaseViewHolder, item: AddFriendRequest) {
        helper.setText(R.id.userNameText, item.fromUserName)
                .setText(R.id.messageText, item.message)
        GlideApp.with(mContext)
                .load(MyUtils.getImageUrl(item.fromTelephone))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_user_image)
                .into(helper.getView(R.id.userImageView))

    }
}
