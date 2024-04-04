package dev.haqim.dailytasktracker.domain.usecase

import android.util.Log
import androidx.paging.PagingData
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetTasksUseCase  @Inject constructor(
    private val repository: ITaskRepository
){
    operator fun invoke(categories: List<TaskCategory>, onlyCompleted: Boolean?): Flow<PagingData<Task>> {
        Log.d("hehe GetTasksUseCase", categories.toString())
        return repository.getAllTasks(categories, onlyCompleted)
    }
}