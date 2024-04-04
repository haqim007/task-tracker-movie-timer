package dev.haqim.dailytasktracker.data.local.mapper

import dev.haqim.dailytasktracker.data.local.entity.table.TaskAndCategoryCrossRef
import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.local.entity.view.TaskWithCategoryEntity
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory

fun List<TaskWithCategoryEntity>.toTasks() = this.map {
    it.toTask()
}

fun TaskWithCategoryEntity.toTask() = Task(
    id = this.task.taskId,
    title = this.task.title,
    note = this.task.note,
    attachmentPath = this.task.attachmentPath,
    categories = this.categories.map {
        it.toTaskCategory()
    },
    hasDone = this.task.hasDone
)

fun TaskCategoryEntity.toTaskCategory() = TaskCategory(
    categoryId, title
)

fun Task.toTaskEntity() = TaskEntity(
    taskId = this.id,
    title,
    note,
    attachmentPath,
    hasDone
)

fun TaskCategory.toTaskCategoryEntity() = TaskCategoryEntity(
    id, title
)

fun List<TaskCategory>.toTaskCategoryEntities() = this.map { it.toTaskCategoryEntity() }

fun  List<TaskCategoryEntity>.toTaskAndCategoryCrossRef(taskId: Long) = this.map {
    TaskAndCategoryCrossRef(
        taskId, it.categoryId
    )
}

fun  List<TaskCategoryEntity>.toTaskCategories() = this.map {
    TaskCategory(
        it.categoryId, it.title
    )
}
