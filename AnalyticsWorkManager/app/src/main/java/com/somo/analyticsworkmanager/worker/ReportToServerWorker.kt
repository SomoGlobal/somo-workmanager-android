package com.somo.analyticsworkmanager.worker

import com.somo.analyticsworkmanager.utils.Constants
import com.somo.analyticsworkmanager.ThisApplication
import com.somo.analyticsworkmanager.model.Analytics

import java.io.IOException

import androidx.work.Worker

class ReportToServerWorker : Worker() {
    override fun doWork(): Worker.WorkerResult {
        try {
            val batteryStat = inputData.getString(Constants.BATTERY_PERCENTAGE, "UNKNOWN")
            val netStat = inputData.getLong(Constants.NETWORK_USAGE, 0)

            ThisApplication.thisApplication?.apiHelper?.reportConfig(Analytics(batteryStat, netStat))

            return Worker.WorkerResult.SUCCESS

        } catch (e: IOException) {
            return Worker.WorkerResult.FAILURE
        }

    }
}
