package com.example.farenda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FarendaViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    fun loadTasks() {
        _tasks.value = TaskRepository.getTasks()
    }

    fun toggleTask(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        TaskRepository.updateTask(updatedTask)
        loadTasks()
    }

    fun addTask(title: String, description: String, isCompleted: Boolean) {
        TaskRepository.setTasks(title, description, isCompleted)
        loadTasks()
    }

    fun deleteTask(task: Task) {
        TaskRepository.deleteTask(task)
        loadTasks()
    }
    init {
        loadTasks()
    }
}