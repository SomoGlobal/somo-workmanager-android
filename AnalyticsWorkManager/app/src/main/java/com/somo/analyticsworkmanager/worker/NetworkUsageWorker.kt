package com.somo.analyticsworkmanager.worker

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.telephony.TelephonyManager

import com.somo.analyticsworkmanager.utils.Constants

import androidx.work.Data
import androidx.work.Worker

@RequiresApi(api = Build.VERSION_CODES.M)
class NetworkUsageWorker : Worker() {
    override fun doWork(): Worker.WorkerResult {
        val isNeeded = inputData.getBoolean(Constants.NET_STAT, false)

        if (isNeeded) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                val receivedBytes = getReceivedMobileDataInBytes(networkStatsManager)
                outputData = createOutputData(receivedBytes)
                return Worker.WorkerResult.SUCCESS
            } else {
                return Worker.WorkerResult.FAILURE
            }
        }
        return Worker.WorkerResult.SUCCESS
    }

    private fun createOutputData(bytes: Long?): Data {
        return Data.Builder().putLong(Constants.NETWORK_USAGE, bytes!!).build()
    }

    // READ_PHONE_STATE permission is required
    private fun getSubscriberId(context: Context, networkType: Int): String {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.subscriberId
        }
        return ""
    }


    private fun getReceivedMobileDataInBytes(networkStatsManager: NetworkStatsManager): Long {
        var networkStats: NetworkStats? = null
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(applicationContext, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis(),
                    getPackageUid(applicationContext, applicationContext.packageName))
        } catch (e: RemoteException) {
            return -1
        }

        var rxBytes = 0L
        val bucket = NetworkStats.Bucket()
        while (networkStats!!.hasNextBucket()) {
            networkStats.getNextBucket(bucket)
            rxBytes += bucket.rxBytes
        }
        networkStats.close()
        return rxBytes
    }

    private fun getPackageUid(context: Context, packageName: String): Int {
        val packageManager = context.packageManager
        val uid: Int
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            uid = packageInfo.applicationInfo.uid

        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalStateException("No package UUID found")
        }

        return uid
    }
}
