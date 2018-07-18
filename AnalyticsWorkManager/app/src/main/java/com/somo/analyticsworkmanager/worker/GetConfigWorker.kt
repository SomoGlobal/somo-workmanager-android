package com.somo.analyticsworkmanager.worker

import com.somo.analyticsworkmanager.utils.Constants
import com.somo.analyticsworkmanager.ThisApplication
import com.somo.analyticsworkmanager.model.ConfigResponse
import java.io.IOException

import androidx.work.Data
import androidx.work.Worker

class GetConfigWorker : Worker() {
    override fun doWork(): Worker.WorkerResult {
        try {
            val configResponse = ThisApplication.thisApplication?.apiHelper?.config
            outputData = createOutputData(configResponse!!)
            return Worker.WorkerResult.SUCCESS

        } catch (e: IOException) {
            return Worker.WorkerResult.FAILURE
        }

    }

    private fun createOutputData(configResponse: ConfigResponse): Data {
        return Data.Builder()
                .putBoolean(Constants.BATTERY_STAT, configResponse.isBatteryStatRequired)
                .putBoolean(Constants.NET_STAT, configResponse.isNetUsageStatRequired)
                .build()
    }
}
