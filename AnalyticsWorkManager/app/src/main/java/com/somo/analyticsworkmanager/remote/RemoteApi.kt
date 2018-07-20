package com.somo.analyticsworkmanager.remote

import com.somo.analyticsworkmanager.model.Analytics
import com.somo.analyticsworkmanager.model.ConfigResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RemoteApi {

    companion object {
        const val BASE_API_URL = "https://somo-dummy-api.com/"
        const val ACCEPT_TENANT_UK = "Accept-Tenant: uk"
        const val AUTHORIZATION = "Authorization: Basic xxyyzzxxyyzzxxyyzz"
        const val HOST = "Host: somo-dummy-api.com"
    }

    @get:Headers(ACCEPT_TENANT_UK, AUTHORIZATION, HOST)
    @get:GET("getConfig")
    val config: Call<ConfigResponse>

    @Headers(ACCEPT_TENANT_UK, AUTHORIZATION, HOST)
    @POST("reportConfig")
    fun reportConfig(@Body analytics: Analytics): Call<Void>
}
