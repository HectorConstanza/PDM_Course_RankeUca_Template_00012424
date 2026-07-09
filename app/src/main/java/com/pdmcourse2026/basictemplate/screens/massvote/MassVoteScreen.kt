package com.pdmcourse2026.basictemplate.screens.massvote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdmcourse2026.basictemplate.data.database.entity.QuestionWithOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MassVoteScreen(
    onBack: () -> Unit,
    vm: MassVoteViewModel = viewModel(factory = MassVoteViewModel.Factory)
) {
    val questions by vm.questions.collectAsStateWithLifecycle()
    val isSubmitting by vm.isSubmitting.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    
    val selectedOptions = remember { mutableStateMapOf<Int, Int>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voto Masivo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            if (questions.isNotEmpty()) {
                Surface(tonalElevation = 3.dp) {
                    Button(
                        onClick = { vm.submitVotes(selectedOptions) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = !isSubmitting && selectedOptions.size == questions.size
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Enviar todos los votos")
                        }
                    }
                }
            }
        }
    ) { pad ->
        if (error != null) {
            AlertDialog(
                onDismissRequest = { vm.clearError() },
                title = { Text("Error") },
                text = { Text(error!!) },
                confirmButton = {
                    TextButton(onClick = { vm.clearError() }) {
                        Text("OK")
                    }
                }
            )
        }

        if (questions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Text("No hay preguntas para votar")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(pad).fillMaxSize()
            ) {
                items(questions) { qw ->
                    QuestionVoteCard(
                        questionWithOptions = qw,
                        selectedOptionId = selectedOptions[qw.question.id],
                        onOptionSelected = { optionId ->
                            selectedOptions[qw.question.id] = optionId
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionVoteCard(
    questionWithOptions: QuestionWithOptions,
    selectedOptionId: Int?,
    onOptionSelected: (Int) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = questionWithOptions.question.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            questionWithOptions.options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = option.id == selectedOptionId,
                        onClick = { onOptionSelected(option.id) }
                    )
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
