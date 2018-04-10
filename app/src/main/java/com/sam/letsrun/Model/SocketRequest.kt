package com.sam.letsrun.Model

/**
 * websocket请求
 */
data class SocketRequest(val msg: Int,
                         val telephone: String,
                         val token: String,
                         val info: Any?)