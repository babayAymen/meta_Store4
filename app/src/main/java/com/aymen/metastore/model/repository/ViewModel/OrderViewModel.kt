package com.aymen.store.model.repository.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.entity.api.PurchaseOrderDto
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm
) :ViewModel(){

    var orders by mutableStateOf(emptyList<PurchaseOrderLine>())
    val companyId by mutableLongStateOf(1)

}