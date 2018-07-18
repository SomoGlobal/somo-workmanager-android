package com.somo.analyticsworkmanager.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import com.somo.analyticsworkmanager.worker.BatteryUsageWorker
import com.somo.analyticsworkmanager.worker.GetConfigWorker
import com.somo.analyticsworkmanager.worker.NetworkUsageWorker
import com.somo.analyticsworkmanager.worker.ReportToServerWorker

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import androidx.work.WorkStatus


class HomeViewModel : ViewModel() {

    val workManager: WorkManager
    var outputStatus: LiveData<List<WorkStatus>>

    companion object {
        private val TAG_OUTPUT = "OUTPUT"
        private val TAG_UNIQUE_WORK_NAME = "APP_USAGE_ANALYTIC_MANAGER"
    }

    init {
        workManager = WorkManager.getInstance()
        // This transformation makes sure that whenever the current work Id changes the WorkStatus
        // the UI is listening to changes
        outputStatus = workManager.getStatusesByTag(TAG_OUTPUT)
    }

    fun startWork() {
        val continuation = workManager
                .beginUniqueWork(TAG_UNIQUE_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(GetConfigWorker::class.java))

        val batteryStatBuilder = OneTimeWorkRequest.Builder(BatteryUsageWorker::class.java)

        val netStatBuilder = OneTimeWorkRequest.Builder(NetworkUsageWorker::class.java)

        // Create constraint for to specify battery level and network type
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val reportBuilder = OneTimeWorkRequest.Builder(ReportToServerWorker::class.java)
                .addTag(TAG_OUTPUT)
                .setConstraints(constraints)

        continuation
                .then(batteryStatBuilder.build(), netStatBuilder.build())
                .then(reportBuilder.build())
                .enqueue()
    }
}
