package com.epicdevler.ad.prodigytasks.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epicdevler.ad.prodigytasks.data.models.Todo
import com.epicdevler.ad.prodigytasks.ui.screens.home.TodosVM
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val vm: TodosVM = viewModel()

    LaunchedEffect(key1 = Unit) {
        vm.invoke(TodosVM.Event.Get)
    }

    val uiState = vm.uiState.value

    LaunchedEffect(key1 = uiState.state) {
        if (uiState.state != TodosVM.UiState.State.Idle) {
            Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
        }
    }

    var selectedTodo = remember<Todo?> {
        null
    }
    val todos = uiState.todos
    val bottomSheetState = rememberModalBottomSheetState(
//        initialValue = SheetValue.Hidden,

    )

    val coroutineScope = rememberCoroutineScope()
    val showDeleteDialog = rememberSaveable {
        mutableStateOf(false)
    }

    fun showSheet() {
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }

    fun hideSheet() {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }



    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
        ) {

            Text(
                text = "My Todos",
                style = typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            AnimatedContent(targetState = todos.isEmpty(), label = "") { isEmpty ->
                when (isEmpty) {
                    true -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "No items yet.",
                                style = typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    false -> {

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {


                            todos.sortedBy { it.completed }.groupBy { todo ->
                                todo.completed
                            }
                                .forEach { (t, u) ->
                                    if (t) {
                                        item {
                                            Text(
                                                text = "Completed Tasks",
                                                style = typography.titleMedium,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                    items(items = u) { todo ->
                                        var checked by rememberSaveable {
                                            mutableStateOf(t)
                                        }
                                        TaskItem(
                                            todo = todo,
                                            checked = checked,
                                            onCheckChange = { isChecked -> checked = isChecked },
                                            onAction = { action ->
                                                selectedTodo = todo
                                                when (action) {
                                                    TaskAction.Edit -> {
                                                        showSheet()
                                                    }

                                                    TaskAction.Delete -> {
                                                        showDeleteDialog.value = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                        }
                    }
                }
            }

        }
        Box(modifier = Modifier.padding(16.dp)) {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { showSheet() }

            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = Icons.Rounded.Add.name)
            }
        }
    }
    if (bottomSheetState.currentValue != SheetValue.Hidden) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { hideSheet() },
            dragHandle = {
                Text(
                    text = "What's on your mind?",
                    style = typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        ) {
            var task by rememberSaveable {
                mutableStateOf(selectedTodo?.task ?: "")
            }
            OutlinedTextField(
                label = {
                    Text(text = "What's on your mind?")
                },
                value = task, onValueChange = { task = it },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(16.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        hideSheet()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel")
                }
                Spacer(modifier = Modifier.width(10.dp))
                ElevatedButton(
                    onClick = { vm.invoke(TodosVM.Event.Create(task)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (selectedTodo == null) "Save" else "Update")
                }
            }
        }
    }
    if (showDeleteDialog.value) {
        AlertDialog(

            properties = DialogProperties(),
            onDismissRequest = { showDeleteDialog.value = false },
            confirmButton = {
                ElevatedButton(onClick = { }) {
                    Text(text = "Delete")
                }
            },
            title = {
                Text(text = "Delete Task")
            },
            text = {
                Text(text = "Are you sure you want to delete this task")
            },
            dismissButton = {
                ElevatedButton(onClick = { }) {
                    Text(text = "Cancel")
                }
            }
        )
    }


}

enum class TaskAction {
    Edit, Delete
}

@Composable
fun TaskItem(
    todo: Todo,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    onAction: (TaskAction) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckChange)
        Text(
            text = todo.task,
            style = typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onAction(TaskAction.Edit) }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = Icons.Outlined.Edit.name
            )
        }
        IconButton(onClick = { onAction(TaskAction.Delete) }) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = Icons.Outlined.Delete.name
            )
        }
    }
}