package dev.haqim.dailytasktracker.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.haqim.dailytasktracker.data.preference.DevicePreference
import dev.haqim.dailytasktracker.di.Unscoped
import dev.haqim.dailytasktracker.util.NotificationUtil
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
    private val notificationUtil: NotificationUtil,
    private val devicePreference: DevicePreference
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return null
        
    }
}