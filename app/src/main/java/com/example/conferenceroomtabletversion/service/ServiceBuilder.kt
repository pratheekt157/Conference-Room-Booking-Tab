package com.example.globofly.services

import android.annotation.SuppressLint
import com.example.conferencerommapp.services.ConferenceService
import com.example.conferenceroomtabletversion.helper.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object ServiceBuilder {

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // private lateinit var mContext: Context
    private val okHttp: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(900, TimeUnit.SECONDS)
        .readTimeout(900, TimeUnit.SECONDS)
        .addNetworkInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                return chain.proceed(chain.request().newBuilder().addHeader("Connection", "close").build())
            }


        })


    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constants.IP_ADDRESS)

        .addConverterFactory(GsonConverterFactory.create()).client(okHttp.build())
    private val retrofit: Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

    fun getObject(): ConferenceService {
        return buildService(ConferenceService::class.java)
    }

    /**
     * .addInterceptor {
    it.proceed(
    it.request().newBuilder()
    .addHeader("Token", getTokenFromSharedPreference())
    .addHeader("UserId", getUserIdFromSharedPreference()).build()
    )

    }
     */
}
