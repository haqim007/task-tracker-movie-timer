package dev.haqim.dailytasktracker.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.haqim.dailytasktracker.data.local.entity.table.TaskAndCategoryCrossRef

@Dao
interface TaskAndCategoryCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskAndCategoryCrossRef(crossRef: List<TaskAndCategoryCrossRef>)
    @Query("DELETE FROM tasks_with_categories WHERE task_id = :taskId")
    suspend fun deleteByTaskId(taskId: Long)
    @Query("DELETE FROM tasks_with_categories WHERE category_id = :categoryId")
    suspend fun deleteByCategoryId(categoryId: Long)
}