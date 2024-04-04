package dev.haqim.dailytasktracker.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    fun getAllTasks(categories: List<TaskCategory>, onlyCompleted: Boolean?): Flow<PagingData<Task>>

    fun getTask(taskId: Long): Flow<Task>

    fun deleteTask(task: Task): Flow<Resource<Task>>
    fun insertTask(
        title: String,
        categories: List<TaskCategory>,
        note: String,
        attachmentPath: Uri?,
        hasDone: Boolean = false
    ): Flow<Resource<Task>>
    fun updateTask(task: Task):  Flow<Resource<Task>>

    fun hasDone(task: Task): Flow<Resource<Task>>
}