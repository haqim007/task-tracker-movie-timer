package dev.haqim.dailytasktracker.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: Long,
    val title: String,
    val categories: List<TaskCategory>,
    val note: String,
    val attachmentPath: Uri?,
    val hasDone: Boolean
) : Parcelable
