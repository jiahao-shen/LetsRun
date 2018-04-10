package com.sam.letsrun.View

import com.sam.letsrun.Model.FriendItem
import com.sam.letsrun.Model.SearchUserResponse

/**
 * Created by sam on 2018/3/29.
 */
interface FriendFragmentView {

    fun addFriendResponseError()

    fun showSearchResult(msg: Int, response: SearchUserResponse?)

    fun addFriendSuccess()

    fun loadFriendListError()

    fun loadFriendListSuccess(list: ArrayList<FriendItem>)

}
