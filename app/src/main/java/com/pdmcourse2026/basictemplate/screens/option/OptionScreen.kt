package com.pdmcourse2026.basictemplate.screens.option

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.pdmcourse2026.basictemplate.data.model.Option

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    questionId: Int,
    onBack: () -> Unit,
    vm: OptionsViewModel = viewModel(
        key = "options_$questionId",
        factory = OptionsViewModel.provideFactory(questionId)
    )
) {
    val options by vm.options.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var editingOption by remember { mutableStateOf<Option?>(null) }
    var showSheet by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Opciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        editingOption = null
                        showSheet = true 
                    }) {
                        Icon(Icons.Default.Add, null)
                        Text("Nuevo")
                    }
                }
            )
        }
    ) { pad ->
        if (options.isEmpty()) {
            EmptyState(Modifier.padding(pad))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(pad).fillMaxSize()
            ) {
                items(options, key = { it.id }) { opt ->
                    OptionCard(
                        opt = opt,
                        onEdit = {
                            editingOption = opt
                            showSheet = true
                        },
                        onDelete = { vm.deleteOption(opt) }
                    )
                }
            }
        }
    }

    if (showSheet) {
        OptionBottomSheet(
            option = editingOption,
            onSave = { value, _ -> 
                editingOption?.let { vm.updateOption(it.id, value) } ?: vm.addOption(value)
                showSheet = false
            },
            onDismiss = { showSheet = false }
        )
    }
}

@Composable
private fun OptionCard(opt: Option, onEdit: () -> Unit, onDelete: () -> Unit) {
    ElevatedCard {
        ListItem(
            headlineContent = { Text(opt.name) },
            supportingContent = {
                Column {
                    Text("Votos: ${opt.votes}", style = MaterialTheme.typography.bodyMedium)
                    opt.imageUrl?.let { Text(it, maxLines = 1, style = MaterialTheme.typography.bodySmall) }
                }
            },
            leadingContent = {
                opt.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(12.dp))
        Text("No hay opciones todavía", style = MaterialTheme.typography.titleMedium)
    }
}
