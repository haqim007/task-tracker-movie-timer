package dev.haqim.dailytasktracker.ui.tasks.todo.form.create

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.domain.usecase.AddTaskUseCase
import dev.haqim.dailytasktracker.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase
) : BaseViewModel<AddTaskUiState>() {

    override val _state = MutableStateFlow(AddTaskUiState())

    fun setFile(uri: Uri){
        _state.update {
            it.copy(
                fileURI = uri
            )
        }
    }

    fun removeFile() {
        _state.update {
            it.copy(
                fileURI = null
            )
        }
    }

    fun save(title: String, note: String) {
        _state.update {
            it.copy(
                note = Resource.Success(note)
            )
        }
        if (title.isBlank()){
            _state.update {
                it.copy(
                    title = Resource.Error("Title is required", title)
                )
            }
            return
        }else{
            _state.update {
                it.copy(
                    title = Resource.Success(title)
                )
            }
        }

        state.value.let {
            viewModelScope.launch {
                addTaskUseCase(
                    title = it.title.data!!,
                    note = it.note.data ?: "",
                    fileUri = it.fileURI,
                    categories = it.categories
                ).collectLatest { res ->
                    _state.update {
                        it.copy(submitResult = res)
                    }
                }
            }
        }
    }

    fun addCategory(category: TaskCategory) {
        _state.update {
            it.copy(
                categories = (it.categories + category).distinct()
            )
        }
    }

    fun removeCategory(category: TaskCategory) {
        _state.update {
            it.copy(
                categories = it.categories.filterNot { prevCategory ->
                    prevCategory == category
                }
            )
        }
    }
}

data class AddTaskUiState(
    val fileURI: Uri? = null,
    val title: Resource<String> = Resource.Idle(),
    val note: Resource<String> = Resource.Idle(),
    val categories: List<TaskCategory> = listOf(),
    val submitResult: Resource<Task> = Resource.Idle()
)