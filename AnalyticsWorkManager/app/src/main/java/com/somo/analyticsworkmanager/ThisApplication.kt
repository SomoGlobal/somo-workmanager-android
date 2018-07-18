package com.somo.analyticsworkmanager

import android.app.Application

import com.somo.analyticsworkmanager.remote.ApiHelper
import com.somo.analyticsworkmanager.remote.MockClient
import com.somo.analyticsworkmanager.remote.RemoteApi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ThisApplication : Application() {

    private lateinit var remoteApi: RemoteApi

    lateinit var apiHelper: ApiHelper
        private set

    companion object {
        lateinit var thisApplication: ThisApplication
        private set
    }

    override fun onCreate() {
        super.onCreate()
        setUpApi()
        thisApplication = this
    }

    private fun setUpApi() {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json").build()
            chain.proceed(request)
        }

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logger)
            clientBuilder.addInterceptor(MockClient(applicationContext))
        }

        remoteApi = Retrofit.Builder()
                .baseUrl(RemoteApi.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create(RemoteApi::class.java)

        apiHelper = ApiHelper(remoteApi)
    }
}
