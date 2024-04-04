package dev.haqim.dailytasktracker.domain.usecase

import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.repository.ITaskCategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AddTaskCategoryUseCase @Inject constructor(
    private val repository: ITaskCategoryRepository
){
    suspend operator fun invoke(title: String): Flow<Resource<String>>{
        return repository.insertTaskCategory(title)
    }
}