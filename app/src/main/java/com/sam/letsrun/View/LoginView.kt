package com.sam.letsrun.View

import com.sam.letsrun.Model.LoginResponse

/**
 * Created by sam on 2018/3/10.
 */
interface LoginView {

    fun loginSuccess(loginResponse: LoginResponse)

    fun loginFailed()

    fun telephoneNotExist()

    fun passwordError()

    fun alreadyLogin()

    fun netError()

    fun telephoneNotNull()

    fun telephoneNotValid()

    fun passwordNotNull()

}