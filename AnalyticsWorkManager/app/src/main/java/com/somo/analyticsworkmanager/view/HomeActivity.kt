package com.somo.analyticsworkmanager.view

import android.Manifest
import android.app.AppOpsManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

import android.provider.Settings

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

import android.view.View

import com.somo.analyticsworkmanager.R
import com.somo.analyticsworkmanager.viewModel.HomeViewModel

import androidx.work.WorkStatus
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel

    companion object {
        val REQUEST_READ_PHONE_STATE = 5352
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        buttonPermission.setOnClickListener { requestPermissions() }

        buttonStart.setOnClickListener { startWork() }

        // Show work status
        homeViewModel.outputStatus.observe(this, Observer<List<WorkStatus>>{ listOfWorkStatuses ->

            // Note that these next few lines grab a single WorkStatus if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkStatus is in one location.

            // If there are no matching work statuses, do nothing
            if (listOfWorkStatuses == null || listOfWorkStatuses.isEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workStatus = listOfWorkStatuses.get(0)

            val finished = workStatus.getState().isFinished()
            if (! finished) {
                showWorkInProgress()
            } else {
                showWorkFinished()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.INVISIBLE
        if (arePermissionsGranted()) {
            buttonStart.isEnabled = true
            buttonPermission.isEnabled = false
        } else {
            buttonStart.isEnabled = false
            buttonPermission.isEnabled = true
        }
    }

    private fun requestPermissions() {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), packageName)
        if (mode == AppOpsManager.MODE_ALLOWED) {

            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_READ_PHONE_STATE)
            }

        } else {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), packageName)
        if (mode == AppOpsManager.MODE_ALLOWED) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            return permissionCheck == PackageManager.PERMISSION_GRANTED

        } else {
            return false
        }
    }

    private fun startWork() {
        // Start work
        // Get the ViewModel
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.startWork()
    }

    private fun showWorkInProgress() {
        progressBar.visibility = View.VISIBLE
        buttonStart.isEnabled = false
        buttonPermission.isEnabled = false
    }

    private fun showWorkFinished() {
        progressBar.visibility = View.INVISIBLE
        buttonStart.isEnabled = true
    }
}
