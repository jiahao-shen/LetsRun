package com.sam.letsrun.View

/**
 * Created by sam on 2018/3/17.
 */
interface RegisterFragmentView {

    fun telephoneNotNull()

    fun telephoneNotValid()

    fun codeNotNull()

    fun codeSendSuccess()

    fun codeSendError()

    fun codeCheckSuccess()

    fun codeCheckError()

    fun netError()

    fun telephoneAlreadyExist()

    fun unKnownError()

}