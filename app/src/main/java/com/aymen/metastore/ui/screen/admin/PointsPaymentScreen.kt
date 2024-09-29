package com.aymen.store.ui.screen.admin

import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.model.repository.ViewModel.PointsPaymentViewModel

@Composable
fun PointsPaymentScreen() {

    val pointsPaymentViewModel : PointsPaymentViewModel = viewModel()
    Surface {
//        Button(onClick = { pointsPaymentViewModel.sendPoints() }) {
//
//        }
    }
}