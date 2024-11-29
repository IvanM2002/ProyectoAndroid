package com.example.proyectoandroid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoandroid.data.ShoppingItem
import com.example.proyectoandroid.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: ShoppingViewModel,
    onBackClicked: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Item Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedDate?.convertMillisToDate() ?: "Select Due Date"
                )
            }

            if (showDatePicker) {
                MyDatePicker(
                    onSelectedDate = { dateMillis ->
                        showDatePicker = false
                        selectedDate = dateMillis
                    }
                )
            }

            Button(
                onClick = {
                    if (selectedDate != null) {
                        val date = selectedDate!!.convertMillisToDate()
                        viewModel.addItem(ShoppingItem(description = description, dueDate = date))
                        onBackClicked()
                    } else {
                        println("Error: No date selected")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Item")
            }
        }
    }
}
