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
        // This makes sure that whenever the current workId changes the WorkStatus
        // the UI is listening to changes
        outputStatus = workManager.getStatusesByTag(TAG_OUTPUT)
    }

    /**
     * We are using WorkManager here to get some config from the server,
     * gather device analytics based on the config and report it to the server.
     */
    fun startWork() {
        // Request object to get the config from the server
        val continuation = workManager
                .beginUniqueWork(TAG_UNIQUE_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(GetConfigWorker::class.java))

        // Request object to get the Battery status of the device
        val batteryStatBuilder = OneTimeWorkRequest.Builder(BatteryUsageWorker::class.java)

        // Request object to get the Network usage of the device
        val netStatBuilder = OneTimeWorkRequest.Builder(NetworkUsageWorker::class.java)

        // Create constraint for to specify battery level and network type
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        //Request object to report it to the server
        val reportBuilder = OneTimeWorkRequest.Builder(ReportToServerWorker::class.java)
                .addTag(TAG_OUTPUT)
                .setConstraints(constraints)

        continuation
                // Chaining the GetConfigWorker with BatteryUsageWorker and NetworkUsageWorker
                .then(batteryStatBuilder.build(), netStatBuilder.build()) // Now, gathering analytics will happen in parallel
                .then(reportBuilder.build())  // Chaining the analytics request to server reporting
                .enqueue() // Finally, Don't forget to schedule your work :)
    }
}
