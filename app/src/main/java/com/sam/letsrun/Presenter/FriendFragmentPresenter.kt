package com.sam.letsrun.Presenter

import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.*
import com.sam.letsrun.View.FriendFragmentView
import com.sam.runapp.Net.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by sam on 2018/3/29.
 */
class FriendFragmentPresenter {

    lateinit var mView: FriendFragmentView

    fun searchUser(telephone: String, token: String, query: String) = when {
        !NetworkUtils.isConnected() -> {
            mView.netError()
        }
        telephone == query -> {
            mView.showSearchResult(Const.USER_IS_SELF, null)
        }
        else -> {
            val service = RetrofitUtils.getService()
            val request = hashMapOf("telephone" to telephone, "token" to token, "query" to query)
            service.searchUser(Gson().toJson(request))
                    .enqueue(object : Callback<SearchUserResponse> {
                        override fun onFailure(call: Call<SearchUserResponse>?, t: Throwable?) {
                            mView.unKnownError()
                        }

                        override fun onResponse(call: Call<SearchUserResponse>?, response: Response<SearchUserResponse>?) {
                            if (response == null) {
                                mView.unKnownError()
                                return
                            }
                            val searchUserResponse = response.body() as SearchUserResponse
                            Logger.json(Gson().toJson(searchUserResponse))

                            mView.showSearchResult(searchUserResponse.msg, searchUserResponse)
                        }
                    })
        }
    }

    fun addFriendAnswer(telephone: String, token: String, msg: Int, list: ArrayList<HashMap<String, String>>) {
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val addFriendAnswer = AddFriendResponse(telephone, token, msg, list)

        val service = RetrofitUtils.getService()
        service.addFriendResponse(Gson().toJson(addFriendAnswer))
                .enqueue(object : Callback<SocketResponse> {
                    override fun onFailure(call: Call<SocketResponse>?, t: Throwable?) {
                        mView.unKnownError()
                    }

                    override fun onResponse(call: Call<SocketResponse>?, response: Response<SocketResponse>?) {
                        if (response == null) {
                            mView.unKnownError()
                            return
                        }
                        val addFriendResponse = response.body() as SocketResponse
                        when (addFriendResponse.msg) {
                            Const.ADD_FRIEND_SUCCESS -> mView.addFriendSuccess()

                            Const.UNKNOWN_ERROR -> mView.unKnownError()
                        }
                    }
                })
    }

    fun loadFriendList(telephone: String, token: String) {
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val service = RetrofitUtils.getService()
        val request = hashMapOf("telephone" to telephone, "token" to token)
        service.loadFriendList(Gson().toJson(request))
                .enqueue(object : Callback<LoadFriendListResponse> {
                    override fun onFailure(call: Call<LoadFriendListResponse>?, t: Throwable?) {
                        mView.loadFriendListError()
                    }

                    override fun onResponse(call: Call<LoadFriendListResponse>?, response: Response<LoadFriendListResponse>?) {
                        if (response == null) {
                            mView.unKnownError()
                            return
                        }
                        val loadFriendListResponse = response.body() as LoadFriendListResponse
                        when (loadFriendListResponse.msg) {
                            Const.UNKNOWN_ERROR -> mView.loadFriendListError()

                            Const.LOAD_FRIEND_LIST_SUCCESS -> mView.loadFriendListSuccess(loadFriendListResponse.friendList)
                        }
                    }
                })
    }

}