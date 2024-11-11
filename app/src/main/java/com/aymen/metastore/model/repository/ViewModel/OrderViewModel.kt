package com.aymen.store.model.repository.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: GlobalRepository,
) :ViewModel(){

    var orders by mutableStateOf(emptyList<PurchaseOrderLine>())
    val companyId by mutableLongStateOf(1)

}