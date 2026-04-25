package com.example.mercader.ui.screen.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mercader.common.components.*
import com.example.mercader.common.constants.SliderType

@Composable
fun GameFormScreen(
    viewModel: GameFormViewModel = viewModel(),
    onEventSaved: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onEventSaved()
            viewModel.resetSuccess()
        }
    }

    state.errorMessage?.let { error ->
        LaunchedEffect(error) {
        }
    }

    FormContainer(title = "Formulario de Juego") {

        SectionTitle("Información del juego")

        ThinTextField(
            value = state.title,
            onValueChange = { viewModel.updateGameTitle(it) },
            label = "Nombre del Juego",
        )

        ThickTextField(
            value = state.description,
            onValueChange = { viewModel.updateGameDescription(it) },
            label = "Descripcion del Juego",
            minLines = 3
        )

        ThinTextField(
            value = state.tutorial,
            onValueChange = { viewModel.updateTutorial(it) },
            label = "Enlace de Tutorial",
        )
        CustomSelector(
            value = state.category,
            onValueChange = { viewModel.updateCategory(it) },
            label = "Categoria del juego",
            options = state.gameCategories,
            itemToString = { it.descripcion },
            enabled = true
        )


        EnumSlider(
            sliderType = SliderType.PEOPLE_COUNT,
            value = state.nMinPerson.toFloat(),
            onValueChange = { viewModel.updateMinPerson(it) }
        )

        EnumSlider(
            sliderType = SliderType.PEOPLE_COUNT,
            value = state.nMaxPerson.toFloat(),
            onValueChange = { viewModel.updateMaxPerson(it) }
        )

        EnumSlider(
            sliderType = SliderType.DURATION_HOURS,
            value = state.minMinutes.toFloat(),
            onValueChange = { viewModel.updateMinTime(it) }
        )

        EnumSlider(
            sliderType = SliderType.DURATION_HOURS,
            value = state.maxMinutes.toFloat(),
            onValueChange = { viewModel.updateMaxTime(it) }
        )
        println("Paso Final"+state.difficulties)
        CustomSelector(
            value = state.difficulty,
            onValueChange = { viewModel.updateDifficulty(it) },
            label = "Dificultad del juego",
            options = state.difficulties,
            itemToString = { it.descripcion },
            enabled = true
        )

        CustomSelector(
            value = state.editorial,
            onValueChange = { viewModel.updateEditorial(it) },
            label = "Editorial del juego",
            options = state.editorials,
            itemToString = {it.nombre},
            enabled = true
        )

        NumberTextField(
            value = state.stock.toString(),
            onValueChange = { viewModel.updateStock(it) },
            label = "Stock",
        )

        NumberTextField(
            value = state.price.toString(),
            onValueChange = { viewModel.updatePrice(it) },
            label = "Precio",
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== BOTONES =====
        PrimaryButton(
            text = "Guardar Juego",
            onClick = { viewModel.saveGame() },
            isLoading = state.isSaving,
            enabled = state.title.isNotBlank()
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    )
}