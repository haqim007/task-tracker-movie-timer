package dev.haqim.dailytasktracker.domain.usecase

import android.net.Uri
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: ITaskRepository
){
    suspend operator fun invoke(
        title: String,
        note: String,
        categories: List<TaskCategory>,
        fileUri: Uri? = null
    ): Flow<Resource<Task>>{
        return repository.insertTask(title, categories, note, fileUri)
    }
}