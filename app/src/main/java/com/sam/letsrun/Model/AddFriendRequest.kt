package com.sam.letsrun.Model

/**
 * 添加好友请求
 */
data class AddFriendRequest(val fromTelephone: String,
                            val fromUserName: String,
                            val toTelephone: String,
                            val message: String?)