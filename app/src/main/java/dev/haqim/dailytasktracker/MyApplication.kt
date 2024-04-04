package dev.haqim.dailytasktracker

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dev.haqim.dailytasktracker.worker.AppWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: AppWorkerFactory


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    }
}