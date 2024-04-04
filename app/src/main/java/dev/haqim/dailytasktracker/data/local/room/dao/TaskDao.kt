package dev.haqim.dailytasktracker.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.local.entity.view.TaskWithCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET has_done = :hasDone WHERE task_id = :id")
    suspend fun hasDone(id: Long, hasDone: Boolean)

    @Transaction
    @Query("SELECT * FROM tasks " +
            "WHERE has_done = :onlyCompleted " +
            "limit :limit offset :offset")
    fun getAllTasks(onlyCompleted: Boolean, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM tasks " +
            "limit :limit offset :offset")
    fun getAllTasks(limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>

    @Transaction
    @Query(
        "SELECT * FROM tasks WHERE task_id IN " +
            "(SELECT task_id FROM tasks_with_categories WHERE category_id = :categoryId) " +
                "AND (:onlyCompleted = 1 AND has_done = 1) OR (:onlyCompleted = 0) " +
                "limit :limit offset :offset"
    )
    fun getAllTasks(categoryId: Long, onlyCompleted: Boolean, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>

    @Transaction
    @Query(
        "SELECT * FROM tasks WHERE task_id IN " +
                "(SELECT task_id FROM tasks_with_categories WHERE category_id IN (:categoryIds)) " +
                "limit :limit offset :offset"
    )
    fun getAllTasks(categoryIds: List<Long>,  limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>

    @Transaction
    @Query(
        "SELECT * FROM tasks WHERE task_id IN " +
                "(SELECT task_id FROM tasks_with_categories WHERE category_id IN (:categoryIds)) " +
                "AND has_done = :onlyCompleted "+
                "limit :limit offset :offset"
    )
    fun getAllTasks(categoryIds: List<Long>, onlyCompleted: Boolean, limit: Int, offset: Int): Flow<List<TaskWithCategoryEntity>>


    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    fun getTask(taskId: Long): Flow<TaskWithCategoryEntity>

    @Transaction
    @Query("DELETE  FROM tasks WHERE task_id = :taskId")
    suspend fun deleteTask(taskId: Long)
}