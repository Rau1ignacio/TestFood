package com.example.foodhubtest.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodhubtest.ui.viewmodels.CartVM

@Composable
fun CartScreen(
    vm: CartVM, // <-- CORREGIDO: Recibe el ViewModel directamente
    onConfirmOrder: (Long) -> Unit
) {
    val state by vm.cartState.collectAsState()

    if (state.items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tu carrito está vacío")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.items, key = { it.first.id }) { (product, cartItem) ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text("Cantidad: ${cartItem.quantity}") },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$${product.price * cartItem.quantity}")
                                IconButton(onClick = { vm.removeFromCart(cartItem) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    )
                    Divider()
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Total: $${state.total}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { vm.confirmOrder(onSuccess = onConfirmOrder) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Confirmar Pedido")
            }
        }
    }
}