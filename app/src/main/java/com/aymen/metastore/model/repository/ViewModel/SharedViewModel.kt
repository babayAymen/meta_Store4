package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.aymen.metastore.MainActivity
import com.aymen.metastore.dependencyInjection.TokenManager
import com.aymen.metastore.model.ViewModelRunTracker
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.ui.component.CheckLocation
import com.aymen.store.model.Enum.CompanyCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.realmSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
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
    private  val context: Context,
    private val accountTypeDataStore: DataStore<AccountType>,
    private val tokenManager: TokenManager
): ViewModel() {

    private var _accountType = MutableStateFlow(AccountType.NULL)
    val accountType: StateFlow<AccountType> get() = _accountType

    private var _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user

    private var _company = MutableStateFlow(Company())
    val company: StateFlow<Company> get() = _company

    private var _hisCompany = MutableStateFlow(Company())
    val hisCompany: StateFlow<Company> get() = _hisCompany
    private var _hisUser = MutableStateFlow(User())
    val hisUser: StateFlow<User> get() = _hisUser

    fun setHisCompany(company : Company){
        _hisCompany.value = company
    }
    fun setHisUser(user : User){
        _hisUser.value = user
    }
    fun assignAccountType(accountType: AccountType){
        _accountType.value = accountType
    }
    fun assignCompanyy(company : Company){
        Log.e("assigncompany","c bon company assigned ${company.id}")
        _company.value = company
        Log.e("assigncompany","c bon company assigned value ${this.company.value.id}")
    }
    fun assignUser(user : User){
        _user.value = user
    }

    init {
        viewModelScope.launch {

            _accountType.value = accountTypeDataStore.data.firstOrNull() ?: AccountType.NULL
            _company.value = companyDtoDataStore.data.firstOrNull() ?: Company()
            _user.value = userDtoDatastore.data.firstOrNull() ?: User()
        }

    }
    suspend fun getCompany(): Company {
        return companyDtoDataStore.data.firstOrNull() ?: Company()
    }
    suspend fun getAccountType(): AccountType {
        return accountTypeDataStore.data.firstOrNull() ?: AccountType.NULL
    }
    suspend fun getUser(): User {
        return userDtoDatastore.data.firstOrNull() ?: User()
    }
    fun updateUserBalance(newBalance : Double){
        viewModelScope.launch {
            try {
                userDtoDatastore.updateData { currentUser ->
                    assignUser(currentUser.copy(
                        balance = newBalance
                    ))
                    currentUser.copy(
                        balance = newBalance
                    )
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
                    assignCompanyy(currentCompany.copy (
                        balance = newBalance
                    ))
                    currentCompany.copy (
                        balance = newBalance
                        )
                }
            }catch (ex : Exception){
                Log.e("updateUserBalance","exception :${ex.message}")
            }
        }
    }


    fun changeAccountType(type : AccountType){
            changeAccount(type)

    }

    fun updateBalance(balancee : BigDecimal){
        if(accountType.value == AccountType.USER){
            updateUserBalance(balancee.toDouble())
        }
        if(accountType.value == AccountType.COMPANY){
            updateCompanyBalance(balancee.toDouble())
        }
    }

    fun returnThePrevioseBalance(newBalance : BigDecimal){
        if(accountType.value == AccountType.USER){
            val balancee = BigDecimal(_user.value.balance!!) + newBalance
            updateUserBalance(balancee.toDouble())
        }
        if(accountType.value == AccountType.COMPANY){
            val balancee = BigDecimal(_company.value.balance!!) + newBalance
            updateCompanyBalance(balancee.toDouble())
        }
    }

    fun logout(){
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.clearToken()
                authDataStore.updateData {
                    it.copy(token = "")
                }
                companyDtoDataStore.updateData {
                    Company(
                        id = 0,
                        name = "",
                        code = "",
                        matfisc = "",
                        address = "",
                        phone = "",
                        bankaccountnumber = "",
                        email = "",
                        capital = "",
                        logo = "",
                        workForce = 0,
                        rate = 0.0,
                        raters = 0,
                        category = CompanyCategory.DAIRY,
                        user = User(),
                        longitude = 0.0,
                        latitude = 0.0
                    )
                }
                userDtoDatastore.updateData {
                    User(
                        id = 0,
                        username = "",
                        address = "",
                        phone = "",
                        balance = 0.0,
                        image = "",
                        longitude = 0.0,
                        latitude = 0.0
                    )
                }
                accountTypeDataStore.updateData {
                    AccountType.NULL
                }
                roomBlock()
            withContext(Dispatchers.Main){
                restartApp()
            }
        }
    }

    fun changeAccount(type : AccountType){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountTypeDataStore.updateData {
                    type
                }
            }catch (ex : Exception){

            }
            roomBlock()
            withContext(Dispatchers.Main){
                restartApp()
            }
        }
    }

    private fun roomBlock(){
        viewModelScope.launch(Dispatchers.IO) {
            room.withTransaction {
                room.clearAllTables()
            }
        }
    }
    private fun restartApp(){
        val intent = Intent(context, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)

    }


}