package com.pdmcourse2026.basictemplate.screens.option

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdmcourse2026.basictemplate.data.model.Option

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionBottomSheet(
    option: Option? = null,
    onSave: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var nameText by rememberSaveable { mutableStateOf(option?.name ?: "") }
    var imageUrl by rememberSaveable { mutableStateOf(option?.imageUrl ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (option == null) "Nueva Opción" else "Editar Opción",
                style = MaterialTheme.typography.titleLarge
            )
            
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL Imagen (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { 
                    onSave(nameText.trim(), imageUrl.trim().takeIf { it.isNotEmpty() })
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nameText.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}
