package com.sam.letsrun.Model

/**
 * 加载好友列表响应
 */
data class LoadFriendListResponse(val msg: Int,
                                  val friendList: ArrayList<Friend>)