package com.example.mercader.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserBottomNav(
    onInProgress: () -> Unit,
    onSearch: (String) -> Unit,
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIconButton(
                icon = "🏠",
                label = "Inicio",
                onClick = onInProgress
            )
            NavIconButton(
                icon = "🧭",
                label = "Explorar",
                onClick = {onSearch(query)}
            )
            NavIconButton(
                icon = "🛒",
                label = "Carrito",
                onClick = onCartClick
            )
            NavIconButton(
                icon = "👤",
                label = "Perfil",
                onClick = onProfileClick
            )
        }
    }
}