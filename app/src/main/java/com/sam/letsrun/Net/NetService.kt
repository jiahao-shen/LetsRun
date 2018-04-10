package com.sam.letsrun.Net

import com.sam.letsrun.Common.Const
import com.sam.letsrun.Model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 网络接口
 */
interface NetService {
    @Multipart
    @POST("Login.php")
    fun login(@Part("request") request: String): Call<LoginResponse>

    @Multipart
    @POST("Register.php")
    fun checkPhone(@Part("request") request: String): Call<CheckPhoneResponse>

    @Multipart
    @POST("Register.php")
    fun register(@Part("request") request: String, @Part image: MultipartBody.Part): Call<RegisterResponse>

    @Multipart
    @POST("Logout.php")
    fun logout(@Part("request") request: String): Call<LogoutResponse>

    @Multipart
    @POST("LoadInfo.php")
    fun loadInfo(@Part("request") request: String): Call<AdvertisementResponse>


    @Multipart
    @POST("SearchUser.php")
    fun searchUser(@Part("request") request: String): Call<SearchUserResponse>

    @Multipart
    @POST("AddFriendResponse.php")
    fun addFriendAnswer(@Part("request") request: String): Call<SocketResponse>

    @Multipart
    @POST("LoadFriendList.php")
    fun loadFriendList(@Part("request") request: String): Call<LoadFriendListResponse>

    @GET(Const.NEWS_HTTP_ADDRESS)
    fun loadNews(): Call<NewsResponse>
}