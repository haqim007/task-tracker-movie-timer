package dev.haqim.dailytasktracker.data.local.entity.view

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.haqim.dailytasktracker.data.local.entity.table.TaskAndCategoryCrossRef
import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity

data class TaskWithCategoryEntity(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "category_id",
        associateBy = Junction(TaskAndCategoryCrossRef::class)
    )
    val categories: List<TaskCategoryEntity>

)
