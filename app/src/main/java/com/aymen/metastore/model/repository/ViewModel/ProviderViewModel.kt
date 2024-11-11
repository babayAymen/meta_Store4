package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapRelationToRoomRelation
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase
) :ViewModel(){

    private var _providers = MutableStateFlow(emptyList<CompanyWithCompanyClient>())
    val providers : StateFlow<List<CompanyWithCompanyClient>> = _providers
    val companyId by mutableLongStateOf(0)// chenge when i try to change to room

    fun getAllMyProviders(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = repository.getAllMyProvider(companyId)
                    if (response.isSuccessful) {
                        response.body()?.forEach {
                            insertRelation(it)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("getAllMyProviders","exception : ${ex.message}")
                }
                _providers.value = room.clientProviderRelationDao().getAllProvidersByClientId(companyId)
            }
        }
    }

    @Transaction
    suspend fun insertRelation(relation : ClientProviderRelationDto){

        relation.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        relation.client?.let {
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(relation.provider))
        room.clientProviderRelationDao().insertClientProviderRelation(mapRelationToRoomRelation(relation))
    }

    fun addProvider(provider: String, file : File){
        viewModelScope.launch {
            try {
                repository.addProvider(provider,file)
            }catch (_ex : Exception){}
            getAllMyProviders()
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