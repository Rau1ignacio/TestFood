package com.example.foodhubtest.ui.detail

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.ui.viewmodels.CartVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    repo: FoodRepository,
    cartVM: CartVM,
    id: Long,
    onBack: () -> Unit
) {
    var product by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(id) {
        product = repo.getProduct(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        product?.let { p ->
            var added by remember { mutableStateOf(false) }
            val context = LocalContext.current

            Column(Modifier.padding(padding).padding(16.dp)) {
                Text(p.name, style = MaterialTheme.typography.headlineSmall)
                Text("Categoría: ${p.category}")
                Text("$${p.price}")
                Spacer(Modifier.height(12.dp))

                Button(onClick = {
                    cartVM.addToCart(p.id)
                    added = true
                    vibrateOnce(context)
                }) { Text("Añadir al carrito") }

                AnimatedVisibility(visible = added) {
                    Text("¡Añadido!", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

private fun vibrateOnce(ctx: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(50)
    }
}