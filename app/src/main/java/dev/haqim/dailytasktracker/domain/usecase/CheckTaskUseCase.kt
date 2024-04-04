package dev.haqim.dailytasktracker.domain.usecase

import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckTaskUseCase @Inject constructor(
    private val repository: ITaskRepository
){
    operator fun invoke(task: Task, isChecked: Boolean): Flow<Resource<Task>>{
        return repository.hasDone(
            task.copy(hasDone = isChecked)
        )
    }
}