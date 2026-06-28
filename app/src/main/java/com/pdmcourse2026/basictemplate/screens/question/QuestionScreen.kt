package com.pdmcourse2026.basictemplate.screens.question

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdmcourse2026.basictemplate.data.model.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    onQuestionClick: (Int) -> Unit,
    onBack: () -> Unit,
    vm: QuestionViewModel = viewModel(factory = QuestionViewModel.Factory)
) {
    val questions by vm.questions.collectAsStateWithLifecycle()
    val isRefreshing by vm.isRefreshing.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var editingQuestion by remember { mutableStateOf<Question?>(null) }
    var showSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preguntas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        editingQuestion = null
                        showSheet = true 
                    }) {
                        Icon(Icons.Default.Add, null)
                        Text("Nueva")
                    }
                }
            )
        }
    ) { pad ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { vm.refresh() },
            modifier = Modifier.padding(pad).fillMaxSize()
        ) {
            if (error != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error!!,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { vm.refresh() }) {
                        Text("Reintentar")
                    }
                }
            } else if (questions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay preguntas todavía")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(questions, key = { it.id }) { q ->
                        ElevatedCard(onClick = { onQuestionClick(q.id) }) {
                            ListItem(
                                headlineContent = { Text(q.title) },
                                supportingContent = { Text("${q.optionCount} opciones") },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { 
                                            editingQuestion = q
                                            showSheet = true
                                        }) {
                                            Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { vm.deleteQuestion(q) }) {
                                            Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
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

    if (showSheet) {
        QuestionBottomSheet(
            question = editingQuestion,
            onSave = { title ->
                editingQuestion?.let { vm.updateQuestion(it.id, title) } ?: vm.addQuestion(title)
                showSheet = false
            },
            onDismiss = { showSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBottomSheet(
    question: Question?,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(question?.title ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp).padding(bottom = 32.dp).fillMaxWidth()) {
            Text(
                text = if (question == null) "Nueva Pregunta" else "Editar Pregunta",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { onSave(title.trim()) },
                modifier = Modifier.align(Alignment.End).padding(top = 16.dp),
                enabled = title.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}
