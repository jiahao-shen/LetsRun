package com.sam.letsrun.View

import com.sam.letsrun.Model.Friend
import com.sam.letsrun.Model.SearchUserResponse

/**
 * Created by sam on 2018/3/29.
 */
interface FriendFragmentView {

    fun unKnownError()

    fun showSearchResult(msg: Int, response: SearchUserResponse?)

    fun addFriendSuccess()

    fun loadFriendListError()

    fun loadFriendListSuccess(list: ArrayList<Friend>)

    fun netError()

}
