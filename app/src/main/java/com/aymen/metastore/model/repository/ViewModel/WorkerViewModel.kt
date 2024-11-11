package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.converterRealmToApi.mapWorkerToRoomWorker
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Worker
import com.aymen.store.model.entity.dto.WorkerDto
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
                      insertWorker(worker)
                  }
              }
          }  catch (ex : Exception){
              Log.e("getAllWorkers","exception : ${ex.message}")
          }
            workers = room.workerDao().getAllWorkors()
        }
    }

    @Transaction
    suspend fun insertWorker(worker : WorkerDto){
        room.userDao().insertUser(mapUserToRoomUser(worker.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(worker.company))
        room.workerDao().insertWorker(mapWorkerToRoomWorker(worker))
    }
}