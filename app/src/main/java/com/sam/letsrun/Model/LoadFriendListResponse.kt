package com.sam.letsrun.Model

/**
 * Created by sam on 2018/4/10.
 */
data class LoadFriendListResponse(val msg: Int,
                                  val friendList: ArrayList<FriendItem>)