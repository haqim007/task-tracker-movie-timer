package dev.haqim.dailytasktracker.data.repository

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.haqim.dailytasktracker.data.local.dataSource.TaskLocalDatasource
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.local.mapper.toTask
import dev.haqim.dailytasktracker.data.local.mapper.toTaskCategoryEntities
import dev.haqim.dailytasktracker.data.local.mapper.toTaskEntity
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.data.pagingsource.TaskPagingSource
import dev.haqim.dailytasktracker.di.DispatcherIO
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository
import dev.haqim.dailytasktracker.util.DEFAULT_PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val localDatasource: TaskLocalDatasource,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher
): ITaskRepository {

    override fun getAllTasks(
        categories: List<TaskCategory>,
        onlyCompleted: Boolean?
    ): Flow<PagingData<Task>> {
        Log.d("hehe TaskRepository 0", categories.toString())
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false,
                maxSize = 3 * DEFAULT_PAGE_SIZE
            ),
            pagingSourceFactory = {
                Log.d("hehe TaskRepository", categories.toString())
                TaskPagingSource(
                    datasource = localDatasource,
                    categories = categories,
                    onlyCompleted = onlyCompleted
                )
            }
        ).flow
    }


    override fun getTask(taskId: Long): Flow<Task> {
        return localDatasource.getTask(taskId).map {
            it.toTask()
        }.flowOn(dispatcher)
    }

    override fun deleteTask(task: Task): Flow<Resource<Task>> {
        return flow {
            emit(Resource.Loading())
            try {
                withContext(dispatcher) {
                    localDatasource.deleteTask(task.id)
                }
                emit(Resource.Success(task))
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage, null))
            }
        }
    }

    override fun insertTask(
        title: String,
        categories: List<TaskCategory>,
        note: String,
        attachmentPath: Uri?,
        hasDone: Boolean
    ): Flow<Resource<Task>> {
        return flow {
            emit(Resource.Loading())
            try {
                var taskId = -1L
                withContext(dispatcher){
                    taskId = localDatasource.insertTask(
                        TaskEntity(
                            title = title,
                            note = note,
                            attachmentPath = attachmentPath,
                            hasDone = hasDone
                        ),
                        categories = categories.toTaskCategoryEntities()
                    )
                }
                emit(Resource.Success(
                    Task(taskId, title, categories, note, attachmentPath, hasDone)
                ))
            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage, null))
            }
        }
    }

    override fun updateTask(task: Task): Flow<Resource<Task>> {
        return flow {
            emit(Resource.Loading())
            try {
                localDatasource.updateTask(
                    task.toTaskEntity(),
                    categories = task.categories.toTaskCategoryEntities()
                )
                emit(Resource.Success(task))

            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage))
            }
        }.flowOn(dispatcher)
    }

    override fun hasDone(task: Task): Flow<Resource<Task>> {
        return flow {
            emit(Resource.Loading())
            try {
                localDatasource.hasDone(
                    task.toTaskEntity()
                )
                emit(Resource.Success(task))

            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage))
            }
        }.flowOn(dispatcher)
    }
}