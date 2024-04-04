package dev.haqim.dailytasktracker.ui.tasks.todo.list

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.usecase.CheckTaskUseCase
import dev.haqim.dailytasktracker.domain.usecase.GetTasksUseCase
import dev.haqim.dailytasktracker.ui.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val checkTaskUseCase: CheckTaskUseCase
) : BaseViewModel<TaskListUiState>() {
    override val _state = MutableStateFlow(TaskListUiState())
    val pagingFlow: Flow<PagingData<Task>>
        get() = _pagingFlow
    private var _pagingFlow = flowOf<PagingData<Task>>(PagingData.empty())

    init {

        _pagingFlow = combine(
                state.map { it.categories },
                state.map { it.onlyCompleted },
                ::Pair
            )
            .distinctUntilChanged()
            .flatMapLatest {
                getTasksUseCase(it.first, it.second)
                    .cachedIn(viewModelScope)
            }
    }

    fun addFilterCategories(category: TaskCategory){
        val newCategories = (state.value.categories + category).distinct()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    categories = newCategories
                )
            }

        }

    }

    fun removeFilterCategories(category: TaskCategory){
        val newCategories = (state.value.categories - category).distinct()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    categories = newCategories
                )
            }
        }
    }

    fun check(task: Task, checked: Boolean) {
        checkTaskUseCase(task, checked).launchCollectLatest{}
    }

    fun seeCompletedTasks(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    onlyCompleted = true
                )
            }

        }
    }

    fun seeAllTasks(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    onlyCompleted = null
                )
            }

        }
    }
}

data class TaskListUiState(
    val categories: List<TaskCategory> = listOf(),
    val onlyCompleted: Boolean? = null,
    val updateResult: Resource<Task> = Resource.Idle()
)