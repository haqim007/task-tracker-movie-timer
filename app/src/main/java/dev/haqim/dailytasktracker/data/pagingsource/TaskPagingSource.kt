package dev.haqim.dailytasktracker.data.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.haqim.dailytasktracker.data.local.dataSource.TaskLocalDatasource
import dev.haqim.dailytasktracker.data.local.mapper.toTasks
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.util.DEFAULT_PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TASK_STARTING_PAGE_OFFSET = 0
class TaskPagingSource(
    private val datasource: TaskLocalDatasource,
    private val categories: List<TaskCategory>,
    private val onlyCompleted: Boolean?
): PagingSource<Int, Task>(){

    override fun getRefreshKey(state: PagingState<Int, Task>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Task> {
        val position = params.key ?: TASK_STARTING_PAGE_OFFSET
        val offset = position * DEFAULT_PAGE_SIZE

        return try {
            val data = withContext(Dispatchers.IO){
                if (categories.isEmpty() && onlyCompleted != null){
                    datasource.getAllTasks(onlyCompleted, params.loadSize, offset).first().toTasks()
                }
                else if (categories.isEmpty() && onlyCompleted == null){
                    datasource.getAllTasks(params.loadSize, offset).first().toTasks()
                }
                else if (categories.isNotEmpty() && onlyCompleted != null){
                    datasource.getAllTasks(categories.map { it.id }, onlyCompleted, params.loadSize, offset).first().toTasks()
                }
                else{
                    datasource.getAllTasks(categories.map { it.id }, params.loadSize, offset).first().toTasks()
                }
            }


            Log.d("hehe", data.toString())
            Log.d("hehe TaskPagingSource", categories.toString())

            val nextKey = if(data.isEmpty()){
                null
            }else{
                position + (params.loadSize / DEFAULT_PAGE_SIZE)

            }
            LoadResult.Page(
                data = data,
                prevKey = if (position == TASK_STARTING_PAGE_OFFSET) null else position - 1,
                nextKey = nextKey
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}