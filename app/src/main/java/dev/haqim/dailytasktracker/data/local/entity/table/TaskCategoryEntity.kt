package dev.haqim.dailytasktracker.data.local.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("task_categories")
data class TaskCategoryEntity(
    @PrimaryKey(true)
    @ColumnInfo("category_id")
    val categoryId: Long = 0,
    val title: String
)
