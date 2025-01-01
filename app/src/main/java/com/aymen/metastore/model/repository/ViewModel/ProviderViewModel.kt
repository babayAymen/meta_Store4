package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.ErrorResponse
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderRemoteKeysEntity
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val useCases: MetaUseCases,
    private val sharedViewModel: SharedViewModel,
    private val context: Context
) :ViewModel(){

    private val clientProviderRelationDao = room.clientProviderRelationDao()
    private val companyDao = room.companyDao()

    private val _providerForUpdate : MutableStateFlow<Company> = MutableStateFlow(Company())
    val providerForUpdate : StateFlow<Company> get() = _providerForUpdate

    var update by mutableStateOf(false)

    private val _providers : MutableStateFlow<PagingData<ClientProviderRelation>> = MutableStateFlow(PagingData.empty())
    val providers : StateFlow<PagingData<ClientProviderRelation>> get() = _providers

    val companyId by mutableLongStateOf(0)
    val company : StateFlow<Company?> = MutableStateFlow(sharedViewModel.company.value)

    init {
        getAllMyProviders()
    }
    fun getAllMyProviders(){
        viewModelScope.launch {
            val id = if(sharedViewModel.accountType.value == AccountType.COMPANY) sharedViewModel.company.value.id else sharedViewModel.user.value.id
            Log.e("provideridazeie","company id for probviders $id")
            useCases.getAllMyProviders(id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _providers.value = it
                }
        }
    }

    fun associateProviderForUpdate(item : Company){
        _providerForUpdate.value = item
    }
    fun deleteProvider(item : ClientProviderRelation){
        viewModelScope.launch(Dispatchers.IO) {
            var remoteKey = ProviderRemoteKeysEntity(0,null,null)
            room.withTransaction {
                remoteKey = clientProviderRelationDao.getProviderRemoteKey(item.id!!)
                clientProviderRelationDao.deleteClientProviderRelationById(item.id)
                clientProviderRelationDao.deleteProviderRelationRemoteKey(item.id)
            }
            val result : Result<Response<Void>> = runCatching {
                repository.deleteProvider(item.id!!)
            }
            result.fold(
                onSuccess = {response ->
                    if(response.isSuccessful){
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "delete successul", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        room.withTransaction {
                            clientProviderRelationDao.insertSingleClientProviderRelation(
                                item.toClientProviderRelationEntity()
                            )
                            clientProviderRelationDao.insertSingleProviderRemoteKey(remoteKey)
                        }
                        val errorBodyString = response.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {
                    room.withTransaction {
                        clientProviderRelationDao.insertSingleClientProviderRelation(item.toClientProviderRelationEntity())
                        clientProviderRelationDao.insertSingleProviderRemoteKey(remoteKey)
                    }
                }
            )
        }
    }

    fun addProvider(relation : Company ,provider: String, file : File?){
        viewModelScope.launch(Dispatchers.IO) {
            val clientProvider = ClientProviderRelation()
            val lastCompanyId = companyDao.getLatestCompanyId()
            val companyId = if(lastCompanyId != null) lastCompanyId + 1 else 1
            val lastProviderId = room.clientProviderRelationDao().getLatestProviderId(sharedViewModel.company.value.id!!)
            val id = if(lastProviderId != null) lastProviderId + 1 else 1
            val providerCount = clientProviderRelationDao.getProviderCount(sharedViewModel.company.value.id!!)
               val page = providerCount.div(PAGE_SIZE)
            room.withTransaction {
                companyDao.insertSingleCompany(relation.copy(id = companyId).toCompanyEntity())
                clientProviderRelationDao.insertSingleClientProviderRelation(
                    clientProvider.copy(
                        id = id,
                        client = sharedViewModel.company.value,
                        provider = relation.copy(id = companyId),
                        mvt = 0.0,
                        credit = 0.0,
                        advance = 0.0
                    ).toClientProviderRelationEntity()
                )
                val remoteKey = ProviderRemoteKeysEntity(
                    id = id,
                    prevPage = if(page == 0) null else page,
                    nextPage = null,
                )
                clientProviderRelationDao.insertSingleProviderRemoteKey(remoteKey)
            }
            val response : Result<Response<ClientProviderRelationDto>> = runCatching {
                repository.addProvider(provider, file)
            }
            response.fold(
                onSuccess = {success ->
                    if(success.isSuccessful){
                            val serverResponse = success.body()!!
                            val latestRemoteKey = clientProviderRelationDao.getProviderRemoteKey(id)
                            val remoteKeys = ProviderRemoteKeysEntity(
                                id = serverResponse.id!!,
                                prevPage = latestRemoteKey.prevPage,
                                nextPage = null
                            )
                        room.withTransaction {
                            companyDao.deleteCompanyById(companyId)
                            clientProviderRelationDao.deleteClientProviderRelationById(id)
                            clientProviderRelationDao.deleteProviderRelationRemoteKey(id)
                            companyDao.insertSingleCompany(serverResponse.provider?.toCompany()!!)
                            clientProviderRelationDao.insertSingleClientProviderRelation(
                                serverResponse.toClientProviderRelation()
                            )
                            clientProviderRelationDao.insertSingleProviderRemoteKey(remoteKeys)
                        }
                    }else{
                        Log.e("addprovider","else")
                        companyDao.deleteCompanyById(companyId)
                        clientProviderRelationDao.deleteClientProviderRelationById(id)
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }

                },
                onFailure = {

                }
            )
        }
    }

    fun updateProvider(relation : Company , provider : String , file : File?){
        viewModelScope.launch {
            companyDao.insertSingleCompany(
                if(relation.logo == "")
                    relation.copy(logo = providerForUpdate.value.logo).toCompanyEntity()
                else relation.toCompanyEntity()
            )
            val response : Result<Response<CompanyDto>> = runCatching {
                repository.updateProvider(provider, file)
            }
            response.fold(
                onSuccess = { success ->
                    val result = success.body()
                    if(success.isSuccessful) {
                        if (result != null) {
                            companyDao.insertSingleCompany(result.toCompany())
                        } else {
                            // Handle unexpected null body case
                            companyDao.insertSingleCompany(providerForUpdate.value.toCompanyEntity())
                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "error : ${success.errorBody().toString()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else {
                        // Handle HTTP errors
                        companyDao.insertSingleCompany(providerForUpdate.value.toCompanyEntity())
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)

                    }
                },
                onFailure = {failure ->
                    // Handle exceptions thrown by the API call

                    Log.e("providerCard","result : failur")

                }
            )
        }
    }


    private fun errorBlock(error : String?){
        viewModelScope.launch{
            val re = Gson().fromJson(error, ErrorResponse::class.java)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "error : ${re.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}