package com.sam.runapp.Net

import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Net.NetService
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit工具类
 */
object RetrofitUtils {

    private fun getRetrofit(): Retrofit{
        val okHttpClient = getOkHttpClient()

        return Retrofit.Builder()
                .baseUrl(Const.BASE_HTTP_ADDRESS)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
    }

    fun getWebSocketRequest(): Request = Request.Builder()
            .url(Const.BASE_WEBSOCKET_ADDRESS)
            .build()

    fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .build()

    fun getService(): NetService = getRetrofit().create(NetService::class.java)
}