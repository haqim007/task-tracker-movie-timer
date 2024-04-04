package dev.haqim.dailytasktracker.data.local.dataSource

import androidx.room.withTransaction
import dev.haqim.dailytasktracker.data.local.entity.table.TaskAndCategoryCrossRef
import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.local.entity.view.TaskWithCategoryEntity
import dev.haqim.dailytasktracker.data.local.mapper.toTaskAndCategoryCrossRef
import dev.haqim.dailytasktracker.data.local.room.AppDatabase
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskLocalDatasource @Inject constructor(
    private val database: AppDatabase
) {

    fun getAllTasks(categoryId: Long, onlyCompleted: Boolean, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>{
        return database.taskDao().getAllTasks(categoryId, onlyCompleted, limit, offset)
    }

    fun getAllTasks(categoryIds: List<Long>, onlyCompleted: Boolean, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>{
        return database.taskDao().getAllTasks(categoryIds, onlyCompleted, limit, offset)
    }

    fun getAllTasks(categoryIds: List<Long>, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>{
        return database.taskDao().getAllTasks(categoryIds, limit, offset)
    }

    suspend fun insertTask(task: TaskEntity, categories: List<TaskCategoryEntity>): Long{
        var taskId = -1L
        database.withTransaction {
            taskId = database.taskDao().insertTask(task)
            val crossRefs = categories.map { category ->
                TaskAndCategoryCrossRef(
                    taskId = taskId,
                    categoryId = category.categoryId
                )
            }
            database.taskAndCategoryCrossRefDao().insertTaskAndCategoryCrossRef(crossRefs)


        }
        return taskId
    }

    suspend fun updateTask(task: TaskEntity, categories: List<TaskCategoryEntity>){
        database.withTransaction {
            database.taskDao().updateTask(task)
            deleteTaskAndCategoryCrossRefByTaskId(task.taskId)
            val crossRefs = categories.toTaskAndCategoryCrossRef(taskId = task.taskId)
            database.taskAndCategoryCrossRefDao().insertTaskAndCategoryCrossRef(crossRefs)
        }
    }

    suspend fun hasDone(task: TaskEntity){
        database.withTransaction {
            database.taskDao().hasDone(task.taskId, task.hasDone)
        }
    }

    fun getAllTasks(onlyCompleted: Boolean, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>{
        return database.taskDao().getAllTasks(onlyCompleted, limit, offset)
    }

    fun getAllTasks(limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>{
        return database.taskDao().getAllTasks(limit, offset)
    }

    fun getTask(taskId: Long): Flow<TaskWithCategoryEntity>{
        return database.taskDao().getTask(taskId)
    }

    suspend fun deleteTask(taskId: Long){
        database.taskDao().deleteTask(taskId)
    }

    private suspend fun deleteTaskAndCategoryCrossRefByTaskId(taskId: Long){
        database.taskAndCategoryCrossRefDao().deleteByTaskId(taskId)
    }
}