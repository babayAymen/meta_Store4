package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapInvitationToRoomInvitation
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.converterRealmToApi.mapWorkerToRoomWorker
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.dto.InvitationDto
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
    private val realm : Realm,
    private val room : AppDatabase
): ViewModel() {

    var myAllInvetation by mutableStateOf(emptyList<Invetation>())
    fun getAllMyInvetations(){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val invetations = repository.getAllMyInvetationss()
                    if (invetations.isSuccessful) {
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
                    val response = repository.getAllMyInvetations()
                    if (response.isSuccessful) {
                        response.body()?.forEach {invitation ->
                            insertInvetation(invitation)
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenababyinvetation", "error: $_ex")
                }
                myAllInvetation = repository.getAllMyInvetationsLocally()

        }
    }

    suspend fun insertInvetation(invitation : InvitationDto){
        invitation.companyReceiver?.let {
            room.userDao().insertUser(mapUserToRoomUser(invitation.companyReceiver?.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(invitation.companyReceiver))
        }
        invitation.companySender?.let {
            room.userDao().insertUser(mapUserToRoomUser(invitation.companySender?.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(invitation.companySender))
        }
        invitation.worker?.let {
            room.userDao().insertUser(mapUserToRoomUser(invitation.worker))
        }
        invitation.client?.let {
            room.userDao().insertUser(mapUserToRoomUser(invitation.client))
        }
        room.invetationDao().insertInvitation(mapInvitationToRoomInvitation(invitation))
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