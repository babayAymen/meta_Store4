package com.aymen.store.model.repository.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.entity.realm.Worker
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm
) : ViewModel() {

    var workers by mutableStateOf(emptyList<Worker>())
    val companyId by mutableLongStateOf(1)

    fun getAllMyWorkers(){
        viewModelScope.launch {
          try {
              val worker = repository.getAllMyWorker(companyId)
              if(worker.isSuccessful){
                  worker.body()?.forEach{
                      realm.write {
                          copyToRealm(it, UpdatePolicy.ALL)
                      }
                  }
              }
          }  catch (ex : Exception){}
            workers = repository.getAllMyWorkerLocally()
        }
    }
}