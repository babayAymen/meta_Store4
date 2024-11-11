package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapRelationToRoomRelation
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val companyDataStore: DataStore<CompanyDto>,
    private val appViewModel: AppViewModel,
    private  val sharedViewModel: SharedViewModel
) : ViewModel() {

    private var _myProviders = MutableStateFlow(emptyList<CompanyWithCompanyClient>())
    val myProviders: StateFlow<List<CompanyWithCompanyClient>> = _myProviders

    var allCompanies by mutableStateOf(emptyList<Company>())
    var providerId by mutableLongStateOf(0)
    var parent by mutableStateOf(Company())
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    var update by mutableStateOf(false)

    private val _companiesByArticleId = MutableStateFlow<Map<Long, Company>>(emptyMap())
    val companiesByArticleId: StateFlow<Map<Long, Company>> = _companiesByArticleId

    init {
        getMyCompany()
        getMyCompany {
            sharedViewModel._company.value = it ?: CompanyDto()
        }
    }


    fun addCompany(company: String, file : File){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addCompany(company,file)
            }
        }
    }
 fun updateCompany(company: String, file : File){
     Log.e("aymenbabayupdate","c bon update company $company")
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.updateCompany(company,file)
            }
        }
    }

    fun getAllMyProvider() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = repository.getAllMyProvider(myCompany.id!!).body()!!
                    response.forEach {provider ->
                        insertRelation(provider)
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabay", "error is : $_ex")
                }
                _myProviders.value = room.clientProviderRelationDao().getAllProvidersByClientId(myCompany.id!!)
//                if (providers.isNotEmpty()) {
//                    providerId = providers[0].id!!
//                }
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


    @Transaction
    suspend fun insertCompany(company : CompanyDto){
        room.userDao().insertUser(mapUserToRoomUser(company.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(company))
    }

    fun getMyParent(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            try {
                val response = repository.getMyParent(myCompany.id!!)
                if(response.isSuccessful && response.body() != null){
                   insertCompany(response.body()!!)
                }
            }catch (ex : Exception){
                Log.e("aymenbabayparent","c bon error ${ex.message}")
            }
            parent = room.companyDao().getCompanyById(myCompany.parentCompany?.id!!)
        }
            }
    }

    fun getMyCompany(){
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val response = repository.getMeAsCompany()
                    if (response.isSuccessful) {
                        appViewModel.storeCompany(response.body()!!)
                    }
                } catch (ex: Exception) {
                    Log.e("aymenbabaycompany", "c bon error ${ex.message}")
                }
            }
        }
    }

    fun getAllCompaniesContaining(search : String){
        allCompanies = emptyList()
        viewModelScope.launch {
            try {
                val response = repository.getAllCompaniesContaining(search)
                if(response.isSuccessful){
                    response.body()?.forEach {company ->
                        insertCompany(company)
                    }
                }
            }catch (ex : Exception){
                Log.e("aymenbabaycompanies","error is : ${ex.message}")
            }
                    allCompanies = room.companyDao().getAllCompaniesContaining(search)
        }
    }

     fun getMyCompany(onCompanyRetrieved: (CompanyDto?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
            try {
                companyDataStore.data
                    .catch { exception ->
                        Log.e("getTokenError", "Error getting token: ${exception.message}")
                        onCompanyRetrieved(null)
                    }
                    .collect { company ->
                        onCompanyRetrieved(company)
                    }
            } catch (e: Exception) {
                Log.e("getTokenError", "Error getting token: ${e.message}")
                onCompanyRetrieved(null)
            }
            }
        }
    }

    fun MakeAsPointSeller(status : Boolean, id : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.makeAsPointSeller(status,id)
            }catch (ex : Exception){
                Log.e("getTokenError", "Error getting token: ${ex.message}")
            }
        }
    }

    fun fetchCompanyByArticleId(articleId: Long, companyId: Long) {
        viewModelScope.launch {
            val company = getCompanyByIdLocally(companyId)
            _companiesByArticleId.value = _companiesByArticleId.value + (articleId to company)
        }
    }
    private suspend fun getCompanyByIdLocally(companyId: Long): com.aymen.metastore.model.entity.room.Company {
        return withContext(Dispatchers.IO) {
            room.companyDao().getCompanyById(companyId)
        }
    }
}