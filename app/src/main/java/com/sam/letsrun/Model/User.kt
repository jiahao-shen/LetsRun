package com.sam.letsrun.Model

/**
 * 用户类
 */
data class User(val telephone: String,
                val password: String? = null,
                var username: String,
                val qqid: String? = null,
                val wechatid: String? = null,
                var birthday: String? = null,
                var gender: String? = null,
                var blood: String? = null,
                var height: Int? = null,
                var weight: Int? = null,
                var signature: String? = null,
                var isCountStep: Int = 1,
                var goalSteps: Int = 10000)
