package dev.haqim.dailytasktracker.data.local.dataSource

import androidx.room.Query
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


class TaskCategoryLocalDatasource @Inject constructor(
    private val database: AppDatabase
) {

    suspend fun insertTaskCategory(category: TaskCategoryEntity){
        database.taskCategoryDao().insertTaskCategory(category)
    }

    suspend fun updateCategory(category: TaskCategoryEntity){
        database.withTransaction {
            database.taskCategoryDao().updateTaskCategory(category)
        }
    }

    fun getTaskCategory(categoryId: Long): Flow<TaskCategoryEntity?> {
        return database.taskCategoryDao().getTaskCategory(categoryId)
    }

    fun getTaskCategories(): Flow<List<TaskCategoryEntity>> {
        return database.taskCategoryDao().getTaskCategories()
    }

    suspend fun deleteTaskCategory(categoryId: Long){
        database.taskCategoryDao().deleteTaskCategory(categoryId)
    }

}