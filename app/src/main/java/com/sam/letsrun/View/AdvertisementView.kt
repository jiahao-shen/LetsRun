package com.sam.letsrun.View

import com.sam.letsrun.Model.User

/**
 * Created by sam on 2018/3/26.
 */
interface AdvertisementView {

    fun loadFailed()

    fun loadSuccess(user: User)

    fun netError()

}