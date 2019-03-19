package com.sam.letsrun.View

import com.sam.letsrun.Model.User

interface SettingFragmentView {

    fun logoutSuccess()

    fun logoutFailed()

    fun unKnownError()

    fun netError()

    fun updateUserInfoFailed()

    fun updateUserInfoSuccess(newUser: User)
}