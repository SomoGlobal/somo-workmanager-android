package com.somo.analyticsworkmanager.remote

import com.somo.analyticsworkmanager.model.Analytics
import com.somo.analyticsworkmanager.model.ConfigResponse

import java.io.IOException

class ApiHelper(var remoteApi: RemoteApi) {

    val config: ConfigResponse? get() {
       return remoteApi.config.execute().body()
    }

    fun reportConfig(analytics: Analytics) {
        remoteApi.reportConfig(analytics).execute()
    }
}
