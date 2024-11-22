package com.aymen.store.ui.screen.user

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel

@Composable
fun NotificationScreen(){
    val shop : ShoppingViewModel = hiltViewModel()
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
    val myInvoicesAccepted by invoiceViewModel.myInvoicesAsClient.collectAsStateWithLifecycle()
    val invoicesNotAccepted by invoiceViewModel.allMyInvoiceNotAccepted.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        Text(text = "notification",Modifier.clickable {
        })
    }
}