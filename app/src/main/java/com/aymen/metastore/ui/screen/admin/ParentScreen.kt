package com.aymen.metastore.ui.screen.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ParentCard

@Composable
fun ParentScreen() {
    val appViewModel : AppViewModel = viewModel()
    val companyViewModel : CompanyViewModel = viewModel()
    LaunchedEffect(key1 = true) {
        companyViewModel.getMyParent()
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ){

                ButtonSubmit(labelValue = "get as client", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Column {
                   ParentCard(companyViewModel.parent)
                }
            }

        }
    }
}