package com.example.foodhubtest.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodhubtest.ui.viewmodels.OrderHistoryVM
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(vm: OrderHistoryVM, onBack: () -> Unit) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial de Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no has realizado ningún pedido.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.orders) { order ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        ListItem(
                            headlineContent = { Text("Pedido #${order.id}") },
                            supportingContent = {
                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                Text("Fecha: ${sdf.format(Date(order.timestamp))}")
                            },
                            trailingContent = { Text("Total: $${order.total}") }
                        )
                    }
                }
            }
        }
    }
}