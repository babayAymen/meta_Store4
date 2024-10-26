package com.aymen.metastore.model.repository.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.Location.LocationService
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.dependencyInjection.TokenUtils
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.entity.dto.AuthenticationResponse
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val authDataStore: DataStore<AuthenticationResponse>,
//    private val companyDataStore: DataStore<Company>,
    private val companyDtoDataStore: DataStore<CompanyDto>,
//    private val userDatastore : DataStore<User>,
    private val userDtoDatastore : DataStore<UserDto>,
    private val realm: Realm,
    private val room : AppDatabase,
    private  val context: Context
): ViewModel() {


    var accountType by mutableStateOf(AccountType.USER)

    var isLoading by mutableStateOf(false)
    val _user = MutableStateFlow(UserDto())
    val user: StateFlow<UserDto> = _user

    val _company = MutableStateFlow(CompanyDto())
    val company: StateFlow<CompanyDto> = _company

    fun getMyCompany(onCompanyRetrieved: (CompanyDto?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                try {
//                    companyDataStore.data
//                        .catch { exception ->
//                            Log.e("getTokenError", "Error getting token: ${exception.message}")
//                            onCompanyRetrieved(null)
//                        }
//                        .collect { company ->
////                            onCompanyRetrieved(company)
//                        }
                    companyDtoDataStore.data
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

    fun updateUserBalance(newBalance : Double){
        viewModelScope.launch {
            try {
//                userDatastore.updateData { currentUser ->
//                    currentUser.apply {
//                        balance = newBalance
//                    }.also{
////                        _user.value = currentUser
//                    }
//                }
                userDtoDatastore.updateData { currentUser ->
                    currentUser.copy(
                        balance = newBalance
                    ).also{
                        _user.value = currentUser
                    }
                }
            }catch (ex : Exception){
                Log.e("updateUserBalance","exception :${ex.message}")
            }
        }
    }

    fun updateCompanyBalance(newBalance : Double){
        viewModelScope.launch {
            try {
//                companyDataStore.updateData { currentCompany ->
//                    currentCompany.apply {
//                        balance = newBalance
//                    }.also{
////                        _company.value = currentCompany
//                    }
//                }
                companyDtoDataStore.updateData { currentCompany ->
                    currentCompany.copy (
                        balance = newBalance
                        ).also{
                        _company.value = currentCompany
                    }
                }
            }catch (ex : Exception){
                Log.e("updateUserBalance","exception :${ex.message}")
            }
        }
    }


    fun changeAccountType(type : AccountType){
        accountType = type
         viewModelScope.launch(Dispatchers.IO) {
        realm.write {
            deleteAll()
        }
             room.clearAllTables()
         }
    }

    fun updateBalance(balancee : BigDecimal){
        if(accountType == AccountType.USER){
            updateUserBalance(balancee.toDouble())
        }
        if(accountType == AccountType.COMPANY){
            updateCompanyBalance(balancee.toDouble())
        }
    }

    fun returnThePrevioseBalance(newBalance : BigDecimal){
        if(accountType == AccountType.USER){
            Log.e("cost","userbalance : ${_user.value.balance} new balance : $newBalance")
            val balancee = BigDecimal(_user.value.balance!!) + newBalance
            updateUserBalance(balancee.toDouble())
        }
        if(accountType == AccountType.COMPANY){
            val balancee = BigDecimal(_company.value.balance!!) + newBalance
            updateCompanyBalance(balancee.toDouble())
        }
    }

    fun getToken(onTokenRetrieved: (String?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    authDataStore.data
                        .catch { exception ->
                            Log.e("getTokenError", "Error getting token get token fun in app view model: ${exception.message}")
                            onTokenRetrieved(null)
                        }
                        .collect { authenticationResponse ->
                            Log.e("getToken", "Token: ${authenticationResponse.token}")
                            if(authenticationResponse.token != ""){
                                onTokenRetrieved(authenticationResponse.token)
                            }else{
                                onTokenRetrieved(null)
                            }
                        }
                } catch (e: Exception) {
                    Log.e("getTokenError", "Error getting token get token fun in app view model: ${e.message}")
                    onTokenRetrieved(null)
                }
            }
        }
    }

}