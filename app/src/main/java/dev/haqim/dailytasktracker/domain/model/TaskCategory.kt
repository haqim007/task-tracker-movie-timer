package dev.haqim.dailytasktracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskCategory(
    val id: Long = 0,
    val title: String
) : Parcelable