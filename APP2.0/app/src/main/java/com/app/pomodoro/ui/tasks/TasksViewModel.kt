package com.app.pomodoro.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pomodoro.domain.model.Task
import com.app.pomodoro.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addTask(title: String) {
        viewModelScope.launch {
            repository.upsertTask(
                Task(
                    title = title,
                    createdAt = System.currentTimeMillis(),
                    description = null
                )
            )
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            repository.upsertTask(task.copy(completed = !task.completed))
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteTask(id) }
    }
}

