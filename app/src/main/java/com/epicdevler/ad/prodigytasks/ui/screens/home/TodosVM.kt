package com.epicdevler.ad.prodigytasks.ui.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicdevler.ad.prodigytasks.data.models.Todo
import com.epicdevler.ad.prodigytasks.data.repos.TasksRepository
import com.epicdevler.ad.prodigytasks.data.source.Response
import com.epicdevler.ad.prodigytasks.data.source.models.TodoId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodosVM @Inject constructor(
    private val taskRepo: TasksRepository
) : ViewModel() {

    val uiState: MutableState<UiState> = mutableStateOf(UiState())


    fun invoke(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.Create ->{
                    when(val response = taskRepo.create(event.task)){
                        is Response.Error -> {
                            uiState.value = uiState.value.copy(state = UiState.State.Error, message = response.message)
                        }
                        is Response.Success -> {
                            uiState.value = uiState.value.copy(state = UiState.State.Success, message = response.message)
                        }
                    }
                }
                is Event.Delete -> {

                }
                Event.Get -> {
                    taskRepo.get().collect{
                        when(val response = it){
                            is Response.Error -> {
                                uiState.value = uiState.value.copy(
                                    state = UiState.State.Success,
                                    message = response.message
                                )
                            }
                            is Response.Success -> {
                                uiState.value = uiState.value.copy(
                                    todos = response.data ?: listOf(),
                                    state = UiState.State.Success,
                                    message = response.message
                                )
                            }
                        }
                    }
                }
                is Event.Update -> TODO()
            }
        }
    }

    sealed interface Event {
        class Create(val task: String) : Event
        data object Get : Event
        class Delete(val taskId: TodoId) : Event
        class Update(val task: String, val completed: Boolean, val taskId: TodoId) : Event
    }

    data class UiState(
        val todos: List<Todo> = listOf(),
        val message: String = "",
        val state: State = State.Idle
    ){
        enum class State{
            Success, Error, Idle
        }
    }

}