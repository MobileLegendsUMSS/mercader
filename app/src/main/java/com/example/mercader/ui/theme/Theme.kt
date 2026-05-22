package com.example.mercader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val DarkColorScheme = darkColorScheme(
    primary = Aqua,
    onPrimary = Black,
    primaryContainer = AquaDark,
    onPrimaryContainer = White,

    secondary = Brown,
    onSecondary = Black,
    secondaryContainer = BrownDark,
    onSecondaryContainer = White,

    tertiary = AquaLight,
    onTertiary = Black,
    tertiaryContainer = AquaDark,
    onTertiaryContainer = White,

    background = Black,
    onBackground = BrownLight,
    surface = AquaDark,
    onSurface = BrownLight,

    onError = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Aqua,
    onPrimary = White,
    primaryContainer = AquaLight,
    onPrimaryContainer = Black,

    secondary = Brown,
    onSecondary = White,
    secondaryContainer = BrownLight,
    onSecondaryContainer = Black,

    tertiary = AquaDark,
    onTertiary = White,
    tertiaryContainer = AquaLight,
    onTertiaryContainer = Black,

    background = BrownLight,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    onError = White
)

@Composable
fun MercaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}