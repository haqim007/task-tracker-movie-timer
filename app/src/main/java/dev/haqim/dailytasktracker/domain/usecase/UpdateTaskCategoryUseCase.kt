package dev.haqim.dailytasktracker.domain.usecase

import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.repository.ITaskCategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateTaskCategoryUseCase @Inject constructor(
    private val repository: ITaskCategoryRepository
){
    suspend operator fun invoke(category: TaskCategory): Flow<Resource<TaskCategory>> {
        return repository.updateCategory(category)
    }
}