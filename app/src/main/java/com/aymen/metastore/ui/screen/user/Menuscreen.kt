package com.aymen.store.ui.screen.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        Text(text = "menu")
    }

}