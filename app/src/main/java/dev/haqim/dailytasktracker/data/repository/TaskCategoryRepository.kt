package dev.haqim.dailytasktracker.data.repository

import dev.haqim.dailytasktracker.data.local.dataSource.TaskCategoryLocalDatasource
import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.local.mapper.toTaskCategories
import dev.haqim.dailytasktracker.data.local.mapper.toTaskCategory
import dev.haqim.dailytasktracker.data.local.mapper.toTaskCategoryEntity
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.di.DispatcherIO
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.repository.ITaskCategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.Error
import java.lang.Exception
import javax.inject.Inject

class TaskCategoryRepository @Inject constructor(
    private val localDatasource: TaskCategoryLocalDatasource,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher
): ITaskCategoryRepository {
    override suspend fun insertTaskCategory(title: String): Flow<Resource<String>> {
        return flow {
            try {
                withContext(dispatcher){
                    localDatasource.insertTaskCategory(TaskCategoryEntity(title = title))
                }
                emit(Resource.Success(title))
            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage ?: "Unknown error",title))
            }
        }
    }

    override fun getTaskCategory(categoryId: Long): Flow<TaskCategory?> {
        return localDatasource.getTaskCategory(categoryId).map {
            it?.toTaskCategory()
        }.flowOn(dispatcher)
    }

    override fun getTaskCategories(): Flow<Resource<List<TaskCategory>>> {
        return flow {
            emit(Resource.Loading())
            try {
                localDatasource.getTaskCategories().map {
                    it.toTaskCategories()
                }.flowOn(dispatcher).collect{
                    emit(Resource.Success(it))
                }
            }catch (e: Exception){
                emit(Resource.Error(message = e.localizedMessage ?: "Unknown error"))
            }
        }
    }

    override suspend fun deleteTaskCategory(category: TaskCategory): Flow<Resource<TaskCategory>> {
        return flow{
            try {
                withContext(dispatcher) {
                    localDatasource.deleteTaskCategory(category.id)
                }
                emit(Resource.Success(category))
            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage, category))
            }
        }
    }

    override suspend fun updateCategory(category: TaskCategory): Flow<Resource<TaskCategory>> {
        return flow {
            emit(Resource.Loading())
            try {
                withContext(dispatcher){
                    localDatasource.updateCategory(category = category.toTaskCategoryEntity())
                }
                emit(Resource.Success(category))
            }catch (e: Error){
                emit(Resource.Error(e.localizedMessage, category))
            }
        }
    }

}