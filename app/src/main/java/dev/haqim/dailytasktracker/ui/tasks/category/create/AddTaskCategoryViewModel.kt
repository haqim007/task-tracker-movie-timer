package dev.haqim.dailytasktracker.ui.tasks.category.create

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.usecase.AddTaskCategoryUseCase
import dev.haqim.dailytasktracker.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskCategoryViewModel @Inject constructor(
    private val addTaskCategoryUseCase: AddTaskCategoryUseCase
) : BaseViewModel<AddTaskCategoryUiState>() {
    override val _state = MutableStateFlow(AddTaskCategoryUiState())

    fun save(title: String){
        if (title.isBlank()){
            _state.update {
                it.copy(title = Resource.Error("Title could not be empty", data = title))
            }

            return
        }

        _state.update {
            it.copy(title = Resource.Success(data = title))
        }

        viewModelScope.launch {
            addTaskCategoryUseCase(title).collect{ res ->
                _state.update {
                    it.copy(title = res)
                }
            }
        }

    }
}

data class AddTaskCategoryUiState(
    val title: Resource<String> = Resource.Idle(),
)