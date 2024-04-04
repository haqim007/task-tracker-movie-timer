package dev.haqim.dailytasktracker.ui.tasks.category.list

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.usecase.DeleteTaskCategoryUseCase
import dev.haqim.dailytasktracker.domain.usecase.GetTaskCategoriesUseCase
import dev.haqim.dailytasktracker.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getCategoriesUseCase: GetTaskCategoriesUseCase,
    private val deleteTaskCategoryUseCase: DeleteTaskCategoryUseCase
) : BaseViewModel<CategoryListUiState>() {
    override val _state = MutableStateFlow(CategoryListUiState())

    fun getCategories(){
        viewModelScope.launch {
            getCategoriesUseCase().collect{ res ->
                _state.update {
                    it.copy(categories = res)
                }
            }
        }
    }

    fun deleteCategory(category: TaskCategory){
        viewModelScope.launch {
            deleteTaskCategoryUseCase(category).collect{res ->
                _state.update {
                    it.copy(deleteResult = res)
                }
            }
        }
    }
}

data class CategoryListUiState(
    val categories: Resource<List<TaskCategory>> = Resource.Idle(),
    val deleteResult: Resource<TaskCategory> = Resource.Idle(),
)