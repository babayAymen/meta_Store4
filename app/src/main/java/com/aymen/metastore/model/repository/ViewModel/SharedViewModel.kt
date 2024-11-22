package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val authDataStore: DataStore<AuthenticationResponse>,
    private val companyDtoDataStore: DataStore<Company>,
    private val userDtoDatastore : DataStore<User>,
    private val room : AppDatabase,
    private  val context: Context
): ViewModel() {


    var accountType by mutableStateOf(AccountType.USER)

    var isLoading by mutableStateOf(false)
    val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    val _company = MutableStateFlow(Company())
    val company: StateFlow<Company> = _company

    fun getMyCompany(onCompanyRetrieved: (Company?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                try {
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