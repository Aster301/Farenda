package com.example.farenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.farenda.ui.theme.FarendaTheme

@OptIn(ExperimentalMaterial3Api::class) // Эта аннотация нужна для ModalBottomSheet т.к это экспериментальная функция
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel: FarendaViewModel = viewModel()
    val tasks by viewModel.tasks.observeAsState(emptyList()) // Используем observeAsState для наблюдения за LiveData
    var showSheet by remember { mutableStateOf(false) }    // Состояние для управления панелью
    FarendaTheme {
        Box(modifier = modifier.fillMaxSize()) { // Используем Box, чтобы кнопка была поверх списка
            Column {
                TaskList(
                    tasks = tasks,
                    onToggleTask = { task -> viewModel.toggleTask(task) },
                    onDeleteTask = { task -> viewModel.deleteTask(task) }
                )
            }
            AddTaskButton(onClick = { showSheet = true })
        }

        // Сама панель (BottomSheet)
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
            ) {
                // Содержимое панели
                TaskInputSheet(
                    onSave = { title, description ->
                        viewModel.addTask(title, description, false)
                        showSheet = false // Закрываем после сохранения
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {  // Карточка для отображения задачи
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                text = task.title,
                modifier = Modifier
                .padding(start = 8.dp)
            )
                Text(
                    text = task.description,
                    modifier = Modifier
                    .padding(start = 8.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить задачу"
                )
            }
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, onToggleTask: (Task) -> Unit, onDeleteTask: (Task) -> Unit) { // Список задач
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onToggle = { onToggleTask(task) }, // Переключатель выполнения задачи
                onDelete = { onDeleteTask(task) } // Удаление задачи
            )
        }
    }
}

@Composable
fun AddTaskButton(onClick: () -> Unit){ // Кнопка для добавления задачи
    Box(modifier = Modifier.fillMaxSize()){
        Button(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .size(70.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = onClick
        )
        {
            Text(text = "+", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun TaskInputSheet(onSave: (String, String) -> Unit){  // Панель для редактирования задачи
    var text by remember {mutableStateOf("")}
    var descriptionText by remember { mutableStateOf("") }
    var isDescriptionVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .navigationBarsPadding()
        )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Новая задача", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { isDescriptionVisible = !isDescriptionVisible }) { // Кнопка переключает видимость поля описания
                Icon(
                    imageVector = Icons.Default.DensityMedium,
                    contentDescription = "Добавить описание",
                    tint = if (isDescriptionVisible) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {text = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Заголовок задачи")},
            singleLine = true
        )
        if (isDescriptionVisible){
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descriptionText,
                onValueChange = {descriptionText = it},
                label = { Text("Описание задачи") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSave(text, descriptionText) },
            modifier = Modifier.align(Alignment.End),
            enabled = text.isNotBlank() // Кнопка не нажмется, если пусто
        ) {
            Text("Сохранить")
        }
    }
}
