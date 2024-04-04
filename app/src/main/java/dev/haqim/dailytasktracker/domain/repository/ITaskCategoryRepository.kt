package dev.haqim.dailytasktracker.domain.repository

import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow

interface ITaskCategoryRepository {
    suspend fun insertTaskCategory(title: String): Flow<Resource<String>>

    fun getTaskCategory(categoryId: Long): Flow<TaskCategory?>

    fun getTaskCategories(): Flow<Resource<List<TaskCategory>>>

    suspend fun deleteTaskCategory(category: TaskCategory): Flow<Resource<TaskCategory>>

    suspend fun updateCategory(category: TaskCategory): Flow<Resource<TaskCategory>>
}