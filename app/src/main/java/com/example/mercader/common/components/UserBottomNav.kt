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
    onSwitchToAdmin: () -> Unit,
    onSearch: (String) -> Unit,
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
                icon = "🏠",  // reemplaza: painterResource(R.drawable.ic_home)
                label = "Inicio",
                onClick = onInProgress
            )
            NavIconButton(
                icon = "🧭",  // reemplaza: painterResource(R.drawable.ic_brujula)
                label = "Explorar",
                onClick = {onSearch(query)}
            )
            NavIconButton(
                icon = "🛒",  // reemplaza: painterResource(R.drawable.ic_carrito)
                label = "Carrito",
                onClick = onInProgress
            )
            NavIconButton(
                icon = "👤",  // reemplaza: painterResource(R.drawable.ic_user)
                label = "Perfil",
                onClick = onInProgress
            )

            // cambiar a Admin
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onSwitchToAdmin() }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "⚙️", fontSize = 20.sp)
                Text(
                    text = "Admin",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}