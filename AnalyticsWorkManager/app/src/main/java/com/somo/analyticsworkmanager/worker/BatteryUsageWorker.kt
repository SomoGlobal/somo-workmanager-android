package com.somo.analyticsworkmanager.worker

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

import com.somo.analyticsworkmanager.utils.Constants

import androidx.work.Data
import androidx.work.Worker

class BatteryUsageWorker : Worker() {

    override fun doWork(): Worker.WorkerResult {
        println("BUGG BatteryUsageWorker STARTED")
        val isNeeded = inputData.getBoolean(Constants.BATTERY_STAT, false)

        if (isNeeded) {
            val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = applicationContext.registerReceiver(null, batteryIntentFilter)
            val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPercent = level / scale.toFloat()
            outputData = createOutputData(batteryPercent)
        }
        return Worker.WorkerResult.SUCCESS
    }

    private fun createOutputData(batteryPercent: Float): Data {
        return Data.Builder().putString(Constants.BATTERY_PERCENTAGE, batteryPercent.toString()).build()
    }
}
