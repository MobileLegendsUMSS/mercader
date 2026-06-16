package com.example.mercader.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.data.remote.models.Category
import com.example.mercader.data.remote.models.Editorial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttributesScreen(
    onBack: () -> Unit,
    viewModel: AdminAttributesViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Categorías", "Editoriales")
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    var showEditorialDialog by remember { mutableStateOf(false) }
    var editorialToEdit by remember { mutableStateOf<Editorial?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías y Editoriales") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        categoryToEdit = null
                        showCategoryDialog = true
                    } else {
                        editorialToEdit = null
                        showEditorialDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (selectedTab == 0) {
                CategoriesContent(
                    state = state,
                    onEdit = { category ->
                        categoryToEdit = category
                        showCategoryDialog = true
                    },
                    onDelete = { category ->
                        viewModel.deleteCategory(
                            id = category.id,
                            onSuccess = {
                                Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            } else {
                EditorialsContent(
                    state = state,
                    onEdit = { editorial ->
                        editorialToEdit = editorial
                        showEditorialDialog = true
                    },
                    onDelete = { editorial ->
                        viewModel.deleteEditorial(
                            id = editorial.id,
                            onSuccess = {
                                Toast.makeText(context, "Editorial eliminada", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }

        if (showCategoryDialog) {
            CategoryDialog(
                category = categoryToEdit,
                onDismiss = { showCategoryDialog = false },
                onSave = { descripcion ->
                    if (categoryToEdit == null) {
                        viewModel.createCategory(
                            descripcion = descripcion,
                            onSuccess = {
                                showCategoryDialog = false
                                Toast.makeText(context, "Categoría creada", Toast.LENGTH_SHORT).show()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        )
                    } else {
                        viewModel.updateCategory(
                            id = categoryToEdit!!.id,
                            descripcion = descripcion,
                            onSuccess = {
                                showCategoryDialog = false
                                Toast.makeText(context, "Categoría actualizada", Toast.LENGTH_SHORT).show()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            )
        }

        if (showEditorialDialog) {
            EditorialDialog(
                editorial = editorialToEdit,
                onDismiss = { showEditorialDialog = false },
                onSave = { nombre, pais ->
                    if (editorialToEdit == null) {
                        viewModel.createEditorial(
                            nombre = nombre,
                            pais = pais,
                            onSuccess = {
                                showEditorialDialog = false
                                Toast.makeText(context, "Editorial creada", Toast.LENGTH_SHORT).show()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        )
                    } else {
                        viewModel.updateEditorial(
                            id = editorialToEdit!!.id,
                            nombre = nombre,
                            pais = pais,
                            onSuccess = {
                                showEditorialDialog = false
                                Toast.makeText(context, "Editorial actualizada", Toast.LENGTH_SHORT).show()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CategoriesContent(
    state: AdminAttributesState,
    onEdit: (Category) -> Unit,
    onDelete: (Category) -> Unit
) {
    if (state.isLoadingCategories) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.categories.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay categorías registradas")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.categories) { category ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category.descripcion,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(onClick = { onEdit(category) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onDelete(category) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditorialsContent(
    state: AdminAttributesState,
    onEdit: (Editorial) -> Unit,
    onDelete: (Editorial) -> Unit
) {
    if (state.isLoadingEditorials) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.editorials.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay editoriales registradas")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.editorials) { editorial ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = editorial.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "País: ${editorial.pais}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row {
                            IconButton(onClick = { onEdit(editorial) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onDelete(editorial) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var descripcion by remember { mutableStateOf(category?.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Nueva Categoría" else "Editar Categoría") },
        text = {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(descripcion) },
                enabled = descripcion.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditorialDialog(
    editorial: Editorial?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(editorial?.nombre ?: "") }
    var pais by remember { mutableStateOf(editorial?.pais ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editorial == null) "Nueva Editorial" else "Editar Editorial") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pais,
                    onValueChange = { pais = it },
                    label = { Text("País") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(nombre, pais) },
                enabled = nombre.isNotBlank() && pais.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
