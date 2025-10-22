package com.example.foodhubtest.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodhubtest.ui.viewmodels.AdminVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminListScreen(
    vm: AdminVM,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit
) {
    val products by vm.products.collectAsState()
    val productToDelete by vm.productToDelete.collectAsState()

    // --- DIÁLOGO DE CONFIRMACIÓN PARA BORRAR ---
    // Se muestra solo cuando 'productToDelete' no es nulo.
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { vm.onDeleteCancelled() },
            title = { Text("Confirmar Borrado") },
            text = { Text("¿Estás seguro de que quieres eliminar el producto '${product.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { vm.onDeleteConfirmed() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { vm.onDeleteCancelled() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            // --- MEJORA AÑADIDA: Barra superior para dar contexto a la pantalla ---
            TopAppBar(
                title = { Text("Gestión de Productos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Nuevo Producto")
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos. ¡Añade uno nuevo!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(product.name) },
                            supportingContent = { Text("Stock: ${product.stock} | Precio: $${product.price}") },
                            trailingContent = {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditProduct(product.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar Producto")
                                    }
                                    IconButton(onClick = { vm.onDeleteTriggered(product) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar Producto", tint = MaterialTheme.colorScheme.error)
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