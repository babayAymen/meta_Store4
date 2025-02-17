package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
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
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ClientRemoteKeysEntity
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
class ClientViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
    private  val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases,
    private val context : Context
): ViewModel() {

    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val clientProviderRelationDao = room.clientProviderRelationDao()

    private val _myClients: MutableStateFlow<PagingData<ClientProviderRelation>> = MutableStateFlow(PagingData.empty())
    val myClients: StateFlow<PagingData<ClientProviderRelation>> = _myClients

    private val _myClientsContainingForAutocomplete: MutableStateFlow<PagingData<ClientProviderRelation>> = MutableStateFlow(PagingData.empty())
    val myClientsContainingForAutocomplete: StateFlow<PagingData<ClientProviderRelation>> = _myClientsContainingForAutocomplete


    private val _clientForUpdate : MutableStateFlow<Company?> = MutableStateFlow(Company())
    val clientForUpdate : StateFlow<Company?> get() = _clientForUpdate

    var update by mutableStateOf(false)
    val company: StateFlow<Company?> = sharedViewModel.company
    val user: StateFlow<User?> = sharedViewModel.user
    private val _relationList : MutableStateFlow<List<Invitation>> = MutableStateFlow(emptyList())
    val relationList : StateFlow<List<Invitation>> = _relationList
    fun setRelationList(){
        _relationList.value = emptyList()
    }
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

    fun assignClientForUpdate(client : Company){
        update = true
        _clientForUpdate.value = client
    }

    fun getMyClientForAutocompleteClient(clientName : String){
        viewModelScope.launch {
            Log.e("getAllMyClientContaining","called ${sharedViewModel.company.value.id}")
            useCases.getMyClientForAutocompleteClient(sharedViewModel.company.value.id!!,clientName)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                   _myClientsContainingForAutocomplete.value = it.map { relation -> relation.toClientProviderRelationModel() }
                }
        }
    }

    fun updateClient(company: Company, client: String, file: File?) {
        viewModelScope.launch {
            companyDao.insertSingleCompany(
                if(company.logo == "")
                company.copy(logo = clientForUpdate.value?.logo, isVisible = PrivacySetting.ONLY_ME).toCompanyEntity()
                else company.toCompanyEntity()
            )
            val response : Result<Response<CompanyDto>> = runCatching {
                repository.updateClient(client, file)
            }
            response.fold(
                onSuccess = { success ->
                    val result = success.body()
                    if(success.isSuccessful) {
                        if (result != null) {
                            companyDao.insertSingleCompany(result.toCompany())
                        } else {
                            // Handle unexpected null body case
                            companyDao.insertSingleCompany(clientForUpdate.value?.toCompanyEntity()!!)
                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "error : ${success.errorBody().toString()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else {
                        // Handle HTTP errors
                        companyDao.insertSingleCompany(clientForUpdate.value?.toCompanyEntity()!!)
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)

                    }
                },
                onFailure = {failure ->
                    // Handle exceptions thrown by the API call

                }
            )
        }
    }

        fun addClient(relation : Company ,client: String, file: File?) {
                viewModelScope.launch(Dispatchers.IO) {
                    val clientProvider = ClientProviderRelation()
                val lastCompanyId = companyDao.getLatestCompanyId()
                val companyId = if(lastCompanyId != null) lastCompanyId + 1 else 1
                    val lastClientId = room.clientProviderRelationDao().getLatestClientId(sharedViewModel.company.value.id!!)
                val id = if(lastClientId != null) lastClientId + 1 else 1
                        val clientCount = room.clientProviderRelationDao().getClientCount(sharedViewModel.company.value.id!!)
                        val page = clientCount.div(PAGE_SIZE)
                    room.withTransaction {
                        companyDao.insertSingleCompany(relation.copy(id = companyId).toCompanyEntity())
                        room.clientProviderRelationDao().insertSingleClientProviderRelation(
                            clientProvider.copy(
                                id = id,
                                client = relation.copy(id = companyId),
                                provider = sharedViewModel.company.value,
                                mvt = 0.0,
                                credit = 0.0
                            ).toClientProviderRelationEntity()
                        )
                       val remoteKey = ClientRemoteKeysEntity(
                            id = id,
                            prevPage = if (page == 0) null else page,
                            nextPage = null
                        )
                        room.clientProviderRelationDao().insertSingleClientRemoteKey(remoteKey)
                    }
                    val response : Result<Response<ClientProviderRelationDto>> = runCatching {
                            repository.addClient(client, file)
                    }
                response.fold(
                    onSuccess = {success ->
                        if(success.isSuccessful) {
                            room.withTransaction {
                                val serverRelation = success.body()
                                val latestRemoteKey =
                                    room.clientProviderRelationDao().getClientRemoteKey(id)
                                val remoteKeys = ClientRemoteKeysEntity(
                                    id = serverRelation?.id!!,
                                    prevPage = latestRemoteKey.prevPage,
                                    nextPage = null
                                )
                                companyDao.deleteCompanyById(companyId)
                                clientProviderRelationDao.deleteClientProviderRelationById(id)
                                clientProviderRelationDao.deleteClientRelationRemoteKey(id)
                                companyDao.insertSingleCompany(serverRelation.client?.toCompany()!!)
                                room.clientProviderRelationDao().insertSingleClientRemoteKey(remoteKeys)
                                room.clientProviderRelationDao()
                                    .insertSingleClientProviderRelation(serverRelation.toClientProviderRelation())
                            }
                        }else{
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

    fun deleteClient(relation : ClientProviderRelation){
        viewModelScope.launch(Dispatchers.IO) {
            var remoteKey = ClientRemoteKeysEntity(0,null,null)
            room.withTransaction {
                remoteKey = clientProviderRelationDao.getClientRemoteKey(relation.id!!)
                clientProviderRelationDao.deleteClientProviderRelationById(relation.id)
                clientProviderRelationDao.deleteClientRelationRemoteKey(relation.id)
            }
                val result : Result<Response<Void>> = runCatching {
                    repository.deleteClient(relation.id!!)
                }
                result.fold(
                    onSuccess = {item ->
                        if(item.isSuccessful){
                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "delete successul", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            room.withTransaction {
                                clientProviderRelationDao.insertSingleClientProviderRelation(
                                    relation.toClientProviderRelationEntity()
                                )
                                clientProviderRelationDao.insertSingleClientRemoteKey(remoteKey)
                            }
                            val errorBodyString = item.errorBody()?.string()
                            errorBlock(errorBodyString)
                        }
                    },
                    onFailure = {item ->

                        Log.e("viewmodel","item failure : $item")

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

        fun sendClientRequest(id: Long, type: Type, isDeleted : Boolean) {
            viewModelScope.launch(Dispatchers.IO) {

                    repository.sendClientRequest(id, type, isDeleted)
            }
        }

    fun addAsDelivery(userId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result : Result<Response<AccountType>> = runCatching {
                repository.addAsDelivery(userId)
            }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful){
                        val response = success.body()!!
                    userDao.updateUserAccountType(userId , response)
                    }
                },
                onFailure = {failure ->

                }
            )

        }
    }

    fun checkRelation(id: Long, accountType: AccountType){
        viewModelScope.launch {
            val result = repository.checkRelation(id, accountType)
            if(result.isSuccessful){
                val response = result.body()!!
                if(response.isNotEmpty()){
                    Log.e("testrelation","response : $response")
                    _relationList.value = response.map {
                        it.toInvitationModel()
                    }
                }
            }
        }
    }


}