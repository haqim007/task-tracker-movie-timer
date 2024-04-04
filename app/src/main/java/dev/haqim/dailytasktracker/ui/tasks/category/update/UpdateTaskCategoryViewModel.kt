package dev.haqim.dailytasktracker.ui.tasks.category.update

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.usecase.AddTaskCategoryUseCase
import dev.haqim.dailytasktracker.domain.usecase.UpdateTaskCategoryUseCase
import dev.haqim.dailytasktracker.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateTaskCategoryViewModel @Inject constructor(
    private val updateTaskCategoryUseCase: UpdateTaskCategoryUseCase
) : BaseViewModel<AddTaskCategoryUiState>() {
    override val _state = MutableStateFlow(AddTaskCategoryUiState())

    fun save(title: String){
        if (title.isBlank()){
            _state.update {
                it.copy(
                    category = Resource.Error(
                        "Title could not be empty",
                        data = it.category.data!!.copy(title = title)
                    )
                )
            }

            return
        }

        _state.update {
            it.copy(
                category = Resource.Success(
                    data = it.category.data!!.copy(title = title)
                )
            )
        }

        viewModelScope.launch {
            updateTaskCategoryUseCase(state.value.category.data!!).collect{ res ->
                _state.update {
                    it.copy(category = res)
                }
            }
        }

    }

    fun setCategory(category: TaskCategory) {
        _state.update {
            it.copy(category = Resource.Idle(category))
        }
    }
}

data class AddTaskCategoryUiState(
    val category: Resource<TaskCategory> = Resource.Idle(),
)