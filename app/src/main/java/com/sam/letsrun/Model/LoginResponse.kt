package com.sam.letsrun.Model


/**
 * 登录响应
 */
data class LoginResponse(val msg: Int,
                         val token: String? = null,
                         val user: User? = null)