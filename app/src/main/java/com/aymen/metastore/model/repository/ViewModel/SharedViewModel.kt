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
import com.aymen.store.dependencyInjection.TokenUtils
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.entity.api.AuthenticationResponse
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
    private val companyDataStore: DataStore<Company>,
    private val userDatastore : DataStore<User>,
    private val realm: Realm,
    private  val context: Context
): ViewModel() {


    var accountType by mutableStateOf(AccountType.USER)

    val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    val _company = MutableStateFlow(Company())
    val company: StateFlow<Company> = _company

    fun getMyCompany(onCompanyRetrieved: (Company?) -> Unit) {
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

    fun updateUserBalance(newBalance : Double){
        viewModelScope.launch {
            try {
                userDatastore.updateData { currentUser ->
                    currentUser.apply {
                        balance = newBalance
                    }.also{
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
                companyDataStore.updateData { currentCompany ->
                    currentCompany.apply {
                        balance = newBalance
                    }.also{
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
         viewModelScope.launch {
        realm.write {
            deleteAll()
        }
         }
    }
//    fun getUser(onUserRetrieved: (User?) -> Unit) {
//        viewModelScope.launch(Dispatchers.Main) {
//
//            try {
//                userDataStore.data
//                    .catch { exception ->
//                        Log.e("getTokenError", "Error getting token get user fun in app view model1: ${exception.message}")
//                        onUserRetrieved(null)
//                    }
//                    .collect { response ->
//                        Log.e("getToken", "response user id get user fun in app view model: ${response.id}")
//                        onUserRetrieved(response)
//                        userRole()
//                    }
//            } catch (e: Exception) {
//                Log.e("getTokenError", "Error getting token get user fun in app view model2: ${e.message}")
//                onUserRetrieved(null)
//
//            }
//        }
//    }


//    fun userRole(){
//        viewModelScope.launch {
//            withContext(Dispatchers.Main){
//                var authsize by mutableStateOf(1)
//                getToken {
//                    if (it != null) {
//                        TokenUtils.isUser(it,
//                            authSize = {
//                                    authSize ->
//                                authsize = authSize
//                            })
//                        {isUser ->
//                            when(isUser){
//                                RoleEnum.ADMIN->{
//                                    if (authsize == 1){
//                                        accountType =   AccountType.COMPANY
//                                        storeAccountType(AccountType.COMPANY)
//                                    }else{
//                                        accountType =  AccountType.USER
//                                        storeAccountType(AccountType.USER)
//                                    }
//                                    userRole = isUser
////                                    appViewModel.getMyCompany()
//                                }
//                                else ->{
//                                    userRole = isUser
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

//    @SuppressLint("SuspiciousIndentation")
//    private fun getMyCompany(){
//        viewModelScope.launch {
//            try {
//                val company = repository.getMe()
//                Log.e("aymenbabay", "company id in app view model : $company")
//                if(company.isSuccessful){
//                    Log.e("aymenbabay", "company id in app view model : ${company.body()!!.id}")
//                    storeCompany(company.body()!!)
//                }
//            }catch (ex : Exception){
//                Log.e("exeptions","error is : $ex")
//            }
//        }
//    }

    fun storeCompany(company: Company) {
        viewModelScope.launch {
            try {
//                user = company.user!!
                companyDataStore.updateData{
                    Company().apply {
                        id = company.id
                        name = company.name
                        code = company.code
                        matfisc = company.matfisc
                        address = company.address
                        phone = company.phone
                        bankaccountnumber = company.bankaccountnumber
                        email = company.email
                        capital = company.capital
                        logo = company.logo
                        workForce = company.workForce
                        user = company.user
                        rate = company.rate
                        raters = company.raters
//                        parentCompany = company.parentCompany
                        category = company.category
                        isPointsSeller = company.isPointsSeller
                        balance = company.balance
                    }
                }
            } catch (e: Exception) {
                Log.e("storeTokenError", "Error storing token in store company fun app view model: ${e.message}")
            }
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