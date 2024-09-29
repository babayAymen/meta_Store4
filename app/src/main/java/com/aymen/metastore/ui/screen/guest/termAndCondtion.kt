package com.aymen.store.ui.screen.guest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aymen.store.ui.component.HeadingText
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler

@Composable
fun TermConditionScreen(){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        color = Color.White
    ) {
        Column {
            HeadingText(value = "hello")
        }
        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.SignUpScreen)
        }
    }
}