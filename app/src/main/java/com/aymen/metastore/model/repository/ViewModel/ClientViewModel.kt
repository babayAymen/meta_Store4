package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchType
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
class ClientViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
    private  val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
): ViewModel() {

    private val _myClients: MutableStateFlow<PagingData<ClientProviderRelation>> = MutableStateFlow(PagingData.empty())
    val myClients: StateFlow<PagingData<ClientProviderRelation>> = _myClients

    private val _myClientsContaining: MutableStateFlow<PagingData<SearchHistory>> = MutableStateFlow(PagingData.empty())
    val myClientsContaining: StateFlow<PagingData<SearchHistory>> = _myClientsContaining

    val company: StateFlow<Company?> = sharedViewModel.company
    val user: StateFlow<User?> = sharedViewModel.user
    init {

        getAllMyClient()
    }


    fun getAllMyClient() {
        viewModelScope.launch{
            useCases.getAllMyClient(company.value?.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _myClients.value = it.map { relation -> relation.toClientProviderRelation() }
                }
        }

    }

    fun getAllMyClientContaining(clientname: String) {
        viewModelScope.launch {
            useCases.getAllMyClientContaining(sharedViewModel.company.value.id!!, clientname)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _myClientsContaining.value = it.map { relation -> relation.toSearchHistoryModel() }
                }
        }
    }

        fun addClient(client: String, file: File) {
            viewModelScope.launch {
                try {
                    repository.addClient(client, file)
                } catch (_ex: Exception) {
                }
                getAllMyClient()
            }
        }

        fun addClientWithoutImage(client: String) {
            viewModelScope.launch {
                try {
                    repository.addClientWithoutImage(client)
                } catch (_ex: Exception) {
                }
                getAllMyClient()
            }
        }

        fun sendClientRequest(id: Long, type: Type) {
            Log.e(
                "aymenbabayclient",
                "id : $id type : $type in client view model send client request"
            )
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.sendClientRequest(id, type)
                }
            }
        }





}