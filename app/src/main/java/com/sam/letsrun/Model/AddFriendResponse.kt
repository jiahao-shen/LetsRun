package com.sam.letsrun.Model

/**
 * 添加好友响应
 */
data class AddFriendResponse(val telephone: String,
                             val token: String,
                             val msg: Int,
                             val responseList: ArrayList<HashMap<String, String>>)
