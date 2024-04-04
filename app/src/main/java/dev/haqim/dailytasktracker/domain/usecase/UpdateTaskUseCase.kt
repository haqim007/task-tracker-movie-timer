package dev.haqim.dailytasktracker.domain.usecase

import android.net.Uri
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: ITaskRepository
){
    operator fun invoke(
        task: Task,
        title: String,
        note: String,
        categories: List<TaskCategory>,
        fileUri: Uri? = null
    ): Flow<Resource<Task>> {
        return repository.updateTask(
            task.copy(
                title = title,
                note = note,
                categories = categories,
                attachmentPath = fileUri
            )
        )
    }
}