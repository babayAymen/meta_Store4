package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.Transaction
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val useCases: MetaUseCases,
    private val sharedViewModel: SharedViewModel
) :ViewModel(){

    private var _providers : MutableStateFlow<PagingData<ClientProviderRelation>> = MutableStateFlow(PagingData.empty())
    val providers : StateFlow<PagingData<ClientProviderRelation>> get() = _providers

    val companyId by mutableLongStateOf(0)
    val company : StateFlow<Company?> = MutableStateFlow(sharedViewModel.company.value)

    init {
        getAllMyProviders()
    }
    fun getAllMyProviders(){
        viewModelScope.launch {
            val id = if(sharedViewModel.accountType.value == AccountType.COMPANY) sharedViewModel.company.value.id else sharedViewModel.user.value.id
            useCases.getAllMyProviders(id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _providers.value = it.map { provider -> provider.toClientProviderRelation() }
                }
        }
    }

    fun addProvider(provider: String, file : File){
        viewModelScope.launch {
            try {
                repository.addProvider(provider,file)
            }catch (_ex : Exception){}
        }
    }

    fun addProviderWithoutImage(provider: String){
        viewModelScope.launch {
            try {
                repository.addProviderWithoutImage(provider)
            }catch (_ex : Exception){}
            getAllMyProviders()
        }
    }


}