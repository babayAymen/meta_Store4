package com.aymen.metastore.model.repository.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.MainActivity
import com.aymen.metastore.model.Location.LocationService
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.dependencyInjection.TokenUtils
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.realmSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val dataStore: DataStore<AuthenticationResponse>,
    private val companyDataStore: DataStore<Company>,
    private val userDatastore: DataStore<User>,
    private val sharedViewModel: SharedViewModel,
    private val context: Context,
    private val accountTypeDataStore: DataStore<AccountType>
) : ViewModel(){

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    private var _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user

    private val _currentScreen = mutableStateOf(IconType.HOME)
    val currentScreen: State<IconType> get() = _currentScreen
    var _historySelected = mutableStateOf(currentScreen.value) // Match the type here
    val historySelected: State<IconType> get() = _historySelected

    var userRole by mutableStateOf(RoleEnum.USER)
    var authsize by mutableIntStateOf(1)

    private val _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>> get() = _location
    init {

        // Observing changes to the location LiveData
        location.observeForever { newLocation ->
            logLocationChange(newLocation)
        }
    }


    fun assignUser(user: User){
        _user.value = user
    }
    private fun logLocationChange(newLocation: Pair<Double, Double>) {
        val (latitude, longitude) = newLocation
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            context.startService(this)
        }
        viewModelScope.launch {
            try {
                val response = repository.updateLocations(latitude, longitude)
                assignCordination(latitude, longitude)
                if(response.isSuccessful) {
                    Toast.makeText(context, "you can turn off GPS service", Toast.LENGTH_LONG)
                        .show()
                }
            }catch (ex : Exception){
                Log.e("exeptions","error is : $ex")
            }

        }
    }
    fun updateLocation(location: Pair<Double, Double>) {
        _location.postValue(location)
    }

    private fun assignCordination(latitude : Double, logitude : Double){
        viewModelScope.launch {
        accountTypeDataStore.data.collect { item ->
            when (item) {
                AccountType.COMPANY -> companyDataStore.updateData { company ->
                    company.copy(
                        latitude = latitude,
                        longitude = logitude
                    )
                }
                AccountType.USER -> userDatastore.updateData { user ->
                    user.copy(
                        latitude = latitude,
                        longitude = logitude
                    )
                }
                AccountType.AYMEN -> {}
                AccountType.NULL -> {}
            }
        }
        }
    }

    private val _showCheckLocationDialog = MutableStateFlow(false)
    val showCheckLocationDialog: StateFlow<Boolean> = _showCheckLocationDialog


    fun updateScreen(newValue: IconType) {
        _historySelected.value = currentScreen.value
        _currentScreen.value = newValue
        if(newValue == IconType.MESSAGE && show.value != "message"){
            updateShow("conversation")
        }
        if(newValue == IconType.WALLET && show.value != "payment"){
            updateShow("payment")
        }
    }
    init {
        viewModelScope.launch {
          accountTypeDataStore.data.collect{item ->
              Log.e("accounttype","account : $item")
              if(item == AccountType.NULL){
                  block()
              }else{
                  authsize = 2
                  sharedViewModel.assignAccountType( item)
                  when(item){
                      AccountType.COMPANY -> {
                          companyDataStore.data.collect{
                              sharedViewModel.assignCompany(it)
                              if(it.longitude == 0.0 || it.latitude == 0.0 ){
                                  Log.e("gpstest","company : ${it.latitude} ${it.longitude}")
                                  _showCheckLocationDialog.value = true
                              }
                          }
                      }
                      AccountType.USER -> {
                          userDatastore.data.collect{
                              sharedViewModel.assignUser(it)
                              if(it.longitude == 0.0 || it.latitude == 0.0 ){
                                  Log.e("gpstest","user $it : ${it.latitude} ${it.longitude}")
                                  _showCheckLocationDialog.value = true
                              }
                          }
                      }
                      AccountType.AYMEN -> {}
                      AccountType.NULL -> {}
                  }
              }
          }
      }

    }

     fun block(){
        userRole()
         viewModelScope.launch {
             launch {

                 accountTypeDataStore.data.collect{preferences ->
                     val i = preferences // Here you can access the data from the datastore
                     Log.e("initappviewmodel", "c bon account type $i")
                 }
             }
    launch {

        userDatastore.data.collect { preferences ->
            val i = preferences // Here you can access the data from the datastore
            Log.e("initappviewmodel", "c bon userdto $i")
    }
         }

    launch {

             companyDataStore.data.collect{preferences ->
                 val i = preferences // Here you can access the data from the datastore
                 Log.e("initappviewmodel", "c bon companydto $i")
             }
    }

        }
    }

    private val _show = mutableStateOf("dash")
    val show : State<String> get() = _show

    private val _view = mutableStateOf("payed")
    val view : State<String> get() = _view

    fun updateShow(newValue: String){
        _show.value = newValue
    }
    fun updateView(newValue: String){
        _view.value = newValue
    }



    private fun getMyUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = repository.getMyUserDetails()
                    if (response.isSuccessful) {
                        storeUser(response.body()!!.toUserModel())
                        sharedViewModel.assignUser(response.body()!!.toUserModel())
                    }
                } catch (ex: Exception) {
                    Log.e("getmyuserdetails", "error is : $ex")
                }

        }
    }

     private fun userRole() {
         Log.e("userRole", "Calling userRole function")
         viewModelScope.launch{
             getToken {
                 if (it != null) {
                     TokenUtils.isUser(it,
                         authSize = { authSize ->
                             authsize = authSize
                         })
                     { isUser ->
                         Log.e("userRole", "isUser: $isUser")
                         when (isUser) {
                             RoleEnum.ADMIN -> {
                                 if (authsize == 1) {
                                     Log.e("initappviewmodel","from storing company account")
                                     sharedViewModel.assignAccountType(AccountType.COMPANY)
                                     storeAccountType(AccountType.COMPANY)
                                 } else {
                                     Log.e("initappviewmodel","from storing user account")
                                     sharedViewModel.assignAccountType(AccountType.USER)
                                     storeAccountType(AccountType.USER)
                                 }
                                 getMyCompany()
                             }
                             RoleEnum.USER ->{
                                 sharedViewModel.assignAccountType(AccountType.USER)
                                 storeAccountType(AccountType.USER)
                                 getMyUserDetails()
                             }
                             else -> {
                            //     storeAccountType(AccountType.WORKER)
                                 getMyUserDetails()
                             }
                         }

                         userRole = isUser
                     }
                 }
             }
         }
     }

    fun isLoggedIn(isLogged : (Boolean) -> Unit){
        getToken {
            if (it != null) {
                isLogged(true)
            }else{
                isLogged(false)
            }
        }
    }
     private fun getToken(onTokenRetrieved: (String?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    dataStore.data
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

    private fun storeAccountType(accountType : AccountType){
        viewModelScope.launch (Dispatchers.IO){
            try {
                accountTypeDataStore.updateData {
                    accountType
                }
                Log.e("storeAccount","account type : $accountType")
            }catch (ex : Exception){
                Log.e("errorStoreAccount","error is : $ex")
            }
        }
    }

    private fun storeUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("storeUser", "storeUser image: ${user.image}")
            try {
                userDatastore.updateData{
                    User(
                        id = user.id,
                        username = user.username,
                        address = user.address,
                        phone = user.phone,
                        email = user.email,
                        image = user.image,
                        rate = user.rate,
                        rater = user.rater,
                        balance = user.balance,
                        longitude = user.longitude,
                        latitude = user.latitude
                    )
                }
            } catch (e: Exception) {
                Log.e("storeUserError", "Error storing token store user fun in app view model: ${e.message}")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
     fun getMyCompany(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMeAsCompany()
                if(response.isSuccessful){
                    val company = response.body()!!.toCompanyModel()
                    storeCompany(company)
                    sharedViewModel.assignCompany( response.body()!!.toCompanyModel())
                    storeUser(company.user!!)
                }
            }catch (ex : Exception){
                Log.e("exeptions","error is : $ex")
            }
        }
    }

     fun storeCompany(company: Company) {
        viewModelScope.launch {
            try {
                companyDataStore.updateData{
                    Company(
                        id = company.id,
                        name = company.name,
                        code = company.code,
                        matfisc = company.matfisc,
                        address = company.address,
                        phone = company.phone,
                        bankaccountnumber = company.bankaccountnumber,
                        email = company.email,
                        capital = company.capital,
                        logo = company.logo,
                        workForce = company.workForce,
                        user = company.user!!,
                        rate = company.rate,
                        raters = company.raters,
                        category = company.category!!,
                        isPointsSeller = company.isPointsSeller,
                        balance = company.balance,
                        latitude = company.latitude,
                        longitude = company.longitude,
                        metaSeller = company.metaSeller,
                    )
                }
            } catch (e: Exception) {
                Log.e("storeTokenError", "Error storing token in store company fun app view model: ${e.message}")
            }
        }
    }

    fun updateCompanyName(newName: String, onUpdated: (Company) -> Unit) {
        viewModelScope.launch {
            try {
//                datastore1.updateData { currentCompany ->
//                    currentCompany.apply {
//                        logo = newName
//                    }.also {
////                        onUpdated(currentCompany)
//                    }
//                }
                companyDataStore.updateData { currentCompany ->
                    currentCompany.copy (
                        logo = newName
                    ).also {
                        onUpdated(currentCompany)
                    }
                }
            } catch (e: Exception) {
                Log.e("storeCompanyError", "Error storing company name: ${e.message}")
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                userDatastore.updateData { currentUser ->
                    currentUser.copy (
                        image = newName
                    )
//                        .also {
//                        sharedViewModel.assignUser( currentUser)
//                    }
                }
            } catch (e: Exception) {
                Log.e("storeCompanyError", "Error storing company name: ${e.message}")
            }
        }
    }

    fun updateCompanyBalance(blc : Double){
        viewModelScope.launch {
            companyDataStore.updateData { currentCompany ->
                currentCompany.copy (
                    balance = blc
                )
//                    .also {
//                sharedViewModel.assignCompany(currentCompany)
//                    Log.e("company","company $currentCompany")
//                }
            }
        }
    }


    fun updateImage(file : File){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.updateImage(file)
                if(response.isSuccessful){
                    when(sharedViewModel.accountType.value){
                        AccountType.COMPANY ->{

                            updateCompanyName(file.name){
                                sharedViewModel.assignCompany(it)
                            }
                        }
                        AccountType.USER ->{
                          updateUserName(file.name)
                    }
                        else ->{
                            Log.e("updateImageError", "updateImage in else statement")
                        }                        }
                }
            }catch (ex : Exception){
                Log.e("updateImageError", ex.message.toString())
            }
        }
    }



}