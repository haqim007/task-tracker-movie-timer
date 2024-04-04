package dev.haqim.dailytasktracker.domain.usecase

import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: ITaskRepository
){
    operator fun invoke(task: Task): Flow<Resource<Task>>{
        return repository.deleteTask(task)
    }
}