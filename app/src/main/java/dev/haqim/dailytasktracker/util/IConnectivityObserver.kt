package dev.haqim.dailytasktracker.util

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface IConnectivityObserver {
    fun observer(context: Context): Flow<Status>
    enum class Status{
        Available, Unavailable, Losing, Lost
    }
}