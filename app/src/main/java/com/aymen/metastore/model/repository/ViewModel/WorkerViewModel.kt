package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase
) : ViewModel() {

    var workers by mutableStateOf(emptyList<Worker>())
    val companyId by mutableLongStateOf(1)

    fun getAllMyWorkers(){
        viewModelScope.launch {
          try {
              val response = repository.getAllMyWorker(companyId)
              if(response.isSuccessful){
                  response.body()?.forEach{worker ->
                  }
              }
          }  catch (ex : Exception){
              Log.e("getAllWorkers","exception : ${ex.message}")
          }
        }
    }

}