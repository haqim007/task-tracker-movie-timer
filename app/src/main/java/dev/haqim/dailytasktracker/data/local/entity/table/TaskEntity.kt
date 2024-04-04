package dev.haqim.dailytasktracker.data.local.entity.table

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(true)
    @ColumnInfo(name = "task_id")
    val taskId: Long = 0,
    val title: String,
    val note: String,
    @ColumnInfo(name = "attachment_path")
    val attachmentPath: Uri?,
    @ColumnInfo(name = "has_done")
    val hasDone: Boolean
)
