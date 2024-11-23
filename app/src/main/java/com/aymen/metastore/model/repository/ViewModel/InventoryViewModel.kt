package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.Inventory
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel  @Inject constructor
    (
    private val room : AppDatabase,
    private val useCases: MetaUseCases,
            private val sharedViewModel: SharedViewModel
)
    : ViewModel() {


    private var _inventories : MutableStateFlow<PagingData<Inventory>> = MutableStateFlow(PagingData.empty())
    val inventories: StateFlow<PagingData<Inventory>> = _inventories

    val companyId by mutableLongStateOf(0)

    init {
        getMyInventory()
    }

    fun getMyInventory(){
        viewModelScope.launch {
            useCases.getAllMyInventory(sharedViewModel.company.value.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _inventories.value = it.map { inventory -> inventory.toInventory() }
                }
        }
    }


}