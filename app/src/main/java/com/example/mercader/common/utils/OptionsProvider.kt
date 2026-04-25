package com.example.mercader.common.utils

import com.example.mercader.common.constants.*
// objeto que sirve para obtener los elementos para los selectores

object OptionsProvider {

    fun getGameTypes(): List<String> =
        GameType.values().map { it.displayName }

    fun getDifficulties(): List<String> =
        Difficulties.values().map { it.displayName }

    fun getEditorials(): List<String> =
        Editorial.values().map { it.displayName }

    // ============ MÉTODOS PARA SLIDERS ============

    fun getSliderConfig(sliderType: SliderType): SliderType = sliderType

    fun getAvailableSliderTypes(): List<SliderType> = SliderType.values().toList()

    fun validateSliderValue(sliderType: SliderType, value: Float): Boolean {
        return value in sliderType.range
    }

    fun getDefaultSliderValue(sliderType: SliderType): Float {
        return when (sliderType) {
            SliderType.PEOPLE_COUNT -> 4f
            SliderType.DURATION_HOURS -> 30f
        }
    }
}