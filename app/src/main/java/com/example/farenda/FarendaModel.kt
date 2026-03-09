package com.example.farenda

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import androidx.core.content.edit

data class Task( // Класс для хранения данных задачи
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    var isCompleted: Boolean // Сделали var, чтобы было удобнее менять состояние
)

object TaskRepository { // Класс для управления данными
    private var tasks: MutableList<Task> = mutableListOf()

    // Новые поля для работы с памятью
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private const val PREFS_NAME = "farenda_prefs"
    private const val TASKS_KEY = "tasks_list"

    // Метод для инициализации памяти (нужно вызвать в MainActivity)
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadTasksFromDisk()
    }

    // Метод для записи данных
    private fun saveToDisk() {
        val json = gson.toJson(tasks) // Превращаем список в JSON-строку
        prefs.edit { putString(TASKS_KEY, json) } // Записываем в SharedPreferences
    }

    // Метод для чтения данных с диска
    private fun loadTasksFromDisk() {
        val json = prefs.getString(TASKS_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            tasks = gson.fromJson(json, type) // Превращаем строку обратно в список задач
        }
    }

    fun getTasks(): List<Task> = tasks.toList()

    fun setTasks(title: String, description: String, isCompleted: Boolean) { // Добавление задачи
        tasks.add(Task(title = title, description = description, isCompleted = isCompleted))
        saveToDisk() // Сохраняем после добавления
    }

    fun updateTask(updatedTask: Task) { // Обновление задачи (например, чекбокс)
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
            saveToDisk() // Сохраняем после изменения
        }
    }

    fun deleteTask(deletedTask: Task) { // Удаление задачи
        tasks.removeAll { it.id == deletedTask.id } // Удаляем по уникальному ID
        saveToDisk() // Сохраняем после удаления
    }
}