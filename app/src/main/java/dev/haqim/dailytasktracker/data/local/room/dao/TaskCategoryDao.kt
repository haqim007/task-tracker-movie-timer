package dev.haqim.dailytasktracker.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.local.entity.view.TaskWithCategoryEntity
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskCategory(category: TaskCategoryEntity)

    @Update
    suspend fun updateTaskCategory(category: TaskCategoryEntity)

    @Query("SELECT * FROM task_categories WHERE category_id = :categoryId")
    fun getTaskCategory(categoryId: Long): Flow<TaskCategoryEntity?>

    @Query("SELECT * FROM task_categories")
    fun getTaskCategories(): Flow<List<TaskCategoryEntity>>

    @Transaction
    @Query("DELETE  FROM task_categories WHERE category_id = :categoryId")
    suspend fun deleteTaskCategory(categoryId: Long)
}