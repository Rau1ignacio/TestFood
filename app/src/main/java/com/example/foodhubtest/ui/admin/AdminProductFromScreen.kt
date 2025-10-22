package com.example.foodhubtest.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.foodhubtest.ui.viewmodels.AdminVM
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    vm: AdminVM,
    productId: Long?, // <-- PARÁMETRO AÑADIDO (puede ser nulo para crear)
    navBack: () -> Unit
) {
    val formState by vm.formState.collectAsState()
    val errors = formState.validate()
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Efecto para cargar los datos del producto si estamos en modo "Editar"
    LaunchedEffect(productId) {
        if (productId != null && productId != 0L) {
            vm.loadProductForEdit(productId)
        } else {
            vm.clearForm() // Asegura que el formulario esté vacío para "Crear"
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { vm.onFormChange(formState.copy(photoUri = it.toString())) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { vm.onFormChange(formState.copy(photoUri = it.toString())) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Título dinámico
        Text(
            text = if (formState.id == 0L) "Nuevo Producto" else "Editar Producto",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))

        // --- CAMPO NOMBRE ---
        OutlinedTextField(
            value = formState.name,
            onValueChange = { vm.onFormChange(formState.copy(name = it)) },
            label = { Text("Nombre") },
            isError = errors.containsKey("name"),
            modifier = Modifier.fillMaxWidth()
        )
        errors["name"]?.let { Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(8.dp))

        // --- CAMPO PRECIO ---
        OutlinedTextField(
            value = formState.price,
            onValueChange = { vm.onFormChange(formState.copy(price = it)) },
            label = { Text("Precio (CLP)") },
            isError = errors.containsKey("price"),
            modifier = Modifier.fillMaxWidth()
        )
        errors["price"]?.let { Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(8.dp))

        // --- CAMPO STOCK ---
        OutlinedTextField(
            value = formState.stock,
            onValueChange = { vm.onFormChange(formState.copy(stock = it)) },
            label = { Text("Stock") },
            isError = errors.containsKey("stock"),
            modifier = Modifier.fillMaxWidth()
        )
        errors["stock"]?.let { Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(8.dp))

        // --- CAMPO CATEGORÍA ---
        val categories = listOf("Frutas", "Verduras", "Lácteos", "Carnes", "Otros")
        var isCategoryMenuExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isCategoryMenuExpanded,
            onExpandedChange = { isCategoryMenuExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = formState.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isCategoryMenuExpanded,
                onDismissRequest = { isCategoryMenuExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            vm.onFormChange(formState.copy(category = category))
                            isCategoryMenuExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- SWITCH DISPONIBILIDAD ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = formState.available,
                onCheckedChange = { vm.onFormChange(formState.copy(available = it)) }
            )
            Spacer(Modifier.width(8.dp))
            Text("Disponible para la venta")
        }
        Spacer(Modifier.height(16.dp))

        // --- BOTONES DE IMAGEN ---
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text("Elegir Foto")
            }
            Button(onClick = {
                val uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(context),
                    "com.example.foodhubtest.provider",
                    File.createTempFile("camera_photo_", ".jpg", context.cacheDir)
                )
                tempImageUri = uri
                cameraLauncher.launch(uri)
            }) {
                Text("Tomar Foto")
            }
        }

        if (formState.photoUri != null) {
            Spacer(Modifier.height(8.dp))
            Text("✓ Foto seleccionada", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.weight(1f))

        // --- BOTÓN GUARDAR ---
        Button(
            enabled = formState.isValid,
            onClick = { vm.saveOrUpdateProduct(onSuccess = navBack) }, // Llama a la función correcta
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Guardar Producto")
        }
    }
}