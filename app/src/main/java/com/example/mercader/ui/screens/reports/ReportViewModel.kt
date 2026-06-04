package com.example.mercader.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mercader.data.remote.models.report.*
import com.example.mercader.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _juegosStockState = MutableStateFlow<JuegosStockState>(JuegosStockState.Loading)
    val juegosStockState: StateFlow<JuegosStockState> = _juegosStockState.asStateFlow()

    private val _ingresosPeriodoState = MutableStateFlow<IngresosPeriodoState>(IngresosPeriodoState.Loading)
    val ingresosPeriodoState: StateFlow<IngresosPeriodoState> = _ingresosPeriodoState.asStateFlow()

    private val _categoriasPopularesState = MutableStateFlow<CategoriasPopularesState>(CategoriasPopularesState.Loading)
    val categoriasPopularesState: StateFlow<CategoriasPopularesState> = _categoriasPopularesState.asStateFlow()

    private val _usosPeriodoState = MutableStateFlow<UsosPeriodoState>(UsosPeriodoState.Loading)
    val usosPeriodoState: StateFlow<UsosPeriodoState> = _usosPeriodoState.asStateFlow()

    // Jobs para cancelar la petición anterior antes de lanzar una nueva
    private var ingresosJob: Job? = null
    private var usosJob: Job? = null

    fun loadJuegosStock(allGames: Boolean = true, order: String = "ascendente", amount: Int? = null) {
        viewModelScope.launch {
            _juegosStockState.value = JuegosStockState.Loading
            val result = reportRepository.getJuegosStock(allGames, order, amount)
            result.fold(
                onSuccess = { response ->
                    _juegosStockState.value = if (response.success && response.data.isNotEmpty())
                        JuegosStockState.Success(response.data)
                    else
                        JuegosStockState.Empty(response.message)
                },
                onFailure = { exception ->
                    _juegosStockState.value = JuegosStockState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadIngresosPeriodo(timePeriod: String, timeValue: String) {
        // Cancela la petición anterior si todavía estaba en vuelo
        ingresosJob?.cancel()
        // Resetea a Loading inmediatamente para que Compose lo vea antes de la coroutine
        _ingresosPeriodoState.value = IngresosPeriodoState.Loading
        ingresosJob = viewModelScope.launch {
            val result = reportRepository.getIngresosPeriodo(timePeriod, timeValue)
            result.fold(
                onSuccess = { response ->
                    _ingresosPeriodoState.value = if (response.success && response.data != null)
                        IngresosPeriodoState.Success(response.data.ganancia)
                    else
                        IngresosPeriodoState.Empty(response.message)
                },
                onFailure = { exception ->
                    _ingresosPeriodoState.value = IngresosPeriodoState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadCategoriasPopulares() {
        viewModelScope.launch {
            _categoriasPopularesState.value = CategoriasPopularesState.Loading
            val result = reportRepository.getCategoriasPopulares()
            result.fold(
                onSuccess = { response ->
                    _categoriasPopularesState.value = if (response.success && response.data.isNotEmpty())
                        CategoriasPopularesState.Success(response.data)
                    else
                        CategoriasPopularesState.Empty(response.message)
                },
                onFailure = { exception ->
                    _categoriasPopularesState.value = CategoriasPopularesState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadUsosPeriodo(timePeriod: String, timeValue: String) {
        // Cancela la petición anterior si todavía estaba en vuelo
        usosJob?.cancel()
        // Resetea a Loading inmediatamente para que Compose lo vea antes de la coroutine
        _usosPeriodoState.value = UsosPeriodoState.Loading
        usosJob = viewModelScope.launch {
            val result = reportRepository.getUsosPeriodo(timePeriod, timeValue)
            result.fold(
                onSuccess = { response ->
                    _usosPeriodoState.value = if (response.success && response.data != null)
                        UsosPeriodoState.Success(response.data)
                    else
                        UsosPeriodoState.Empty(response.message)
                },
                onFailure = { exception ->
                    _usosPeriodoState.value = UsosPeriodoState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }
}

sealed class JuegosStockState {
    object Loading : JuegosStockState()
    data class Success(val juegos: List<JuegoStock>) : JuegosStockState()
    data class Empty(val message: String) : JuegosStockState()
    data class Error(val message: String) : JuegosStockState()
}

sealed class IngresosPeriodoState {
    object Loading : IngresosPeriodoState()
    data class Success(val ganancia: Double) : IngresosPeriodoState()
    data class Empty(val message: String) : IngresosPeriodoState()
    data class Error(val message: String) : IngresosPeriodoState()
}

sealed class CategoriasPopularesState {
    object Loading : CategoriasPopularesState()
    data class Success(val categorias: List<CategoriaPopular>) : CategoriasPopularesState()
    data class Empty(val message: String) : CategoriasPopularesState()
    data class Error(val message: String) : CategoriasPopularesState()
}

sealed class UsosPeriodoState {
    object Loading : UsosPeriodoState()
    data class Success(val usos: UsosData) : UsosPeriodoState()
    data class Empty(val message: String) : UsosPeriodoState()
    data class Error(val message: String) : UsosPeriodoState()
}