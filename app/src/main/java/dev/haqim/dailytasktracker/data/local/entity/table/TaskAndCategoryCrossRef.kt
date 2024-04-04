package dev.haqim.dailytasktracker.data.local.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "tasks_with_categories",
    primaryKeys = ["task_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE // Cascade delete when a TaskEntity is deleted
        ),
        ForeignKey(
            entity = TaskCategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE // Cascade delete when a TaskCategoryEntity is deleted
        )
    ],
    indices = [Index("task_id"), Index("category_id")]
)
data class TaskAndCategoryCrossRef(
    @ColumnInfo("task_id")
    val taskId: Long,
    @ColumnInfo("category_id")
    val categoryId: Long
)
