package com.example.mercader.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHeader(title: String = "Merca Herrante",
               isAdmin: Boolean = false,
               onMenuClick: () -> Unit,
               onLogoClick: () -> Unit = {}
              ) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLogoClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            }
        },
        navigationIcon = {
            if (isAdmin) {
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.Default.Menu , contentDescription = "Menu")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}