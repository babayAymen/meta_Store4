package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.realm.Invetation
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class InvetationViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm
): ViewModel() {

    var myAllInvetation by mutableStateOf(emptyList<Invetation>())
    fun getAllMyInvetations(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val invetations = repository.getAllMyInvetations()
                    if (invetations.isSuccessful) {
                        Log.e(
                            "aymenbabayinvetationn",
                            "invetation size from api ip : ${invetations.body()?.get(0)?.client?.id}"
                        )
                        invetations.body()?.forEach {
                            realm.write {
                                val invitation = Invetation().apply {
                                    id = it.id
                                    client = it.client
                                    companySender = it.companySender
                                    companyReciver = it.companyReciver
                                    salary = it.salary
                                    jobtitle = it.jobtitle
                                    department = it.department
                                    totdayvacation = it.totdayvacation
                                    statusvacation = it.statusvacation
                                    status = it.status
                                    type = it.type

                                }
                                copyToRealm(invitation, UpdatePolicy.ALL)
                            }
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenababyinvetation", "error: $_ex")
                }
                myAllInvetation = repository.getAllMyInvetationsLocally()
                Log.e("aymenbabayinvetation", "invetation size: ${myAllInvetation.size}")

            }
        }
    }

    fun RequestResponse(status : Status, id : Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = repository.RequestResponse(status,id)
//                    if (response.isSuccessful) {
//
//                    }
            }catch (_ex: Exception) {
                Log.e("aymenababyinvetation", "error: $_ex")
            }
                }
            repository.deleteInvitation(id)
            myAllInvetation = repository.getAllMyInvetationsLocally()
        }
    }

    fun cancelInvitation(id : Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = repository.cancelInvitation(id)
//                    if (response.isSuccessful) {
//
//                    }
                }catch (_ex: Exception) {
                    Log.e("aymenababyinvetation", "error: $_ex")
                }
            }
            repository.deleteInvitation(id)
            myAllInvetation = repository.getAllMyInvetationsLocally()
        }
    }
}