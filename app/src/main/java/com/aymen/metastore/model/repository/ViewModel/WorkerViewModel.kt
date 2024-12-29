package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val useCases: MetaUseCases,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    private val _workers :MutableStateFlow<PagingData<Worker>> = MutableStateFlow(PagingData.empty())
    val workers : StateFlow<PagingData<Worker>> = _workers


    val company = sharedViewModel.company
    init {
        getAllMyWorkers()
    }
    fun getAllMyWorkers(){
        viewModelScope.launch {
                useCases.getAllWorkers(company.value.id ?: 0)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        _workers.value = it
                    }
        }
    }

}