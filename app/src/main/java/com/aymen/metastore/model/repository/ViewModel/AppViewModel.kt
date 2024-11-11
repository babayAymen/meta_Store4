package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.Location.LocationService
import com.aymen.store.dependencyInjection.TokenUtils
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val dataStore: DataStore<AuthenticationResponse>,
    private val companyDataStore: DataStore<CompanyDto>,
    private val userDatastore: DataStore<UserDto>,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val context: Context
) : ViewModel(){

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _currentScreen = mutableStateOf(IconType.HOME)
    val currentScreen: State<IconType> get() = _currentScreen
    var _historySelected = mutableStateOf(currentScreen.value) // Match the type here
    val historySelected: State<IconType> get() = _historySelected

//    val historySelected : State<IconType> get() = _historySelected

    init {
//        connectWebSocket()
    }

     fun connectWebSocket() {
//        val request = Request.Builder().url("ws://192.168.1.15:8080/ws").build()
         sharedViewModel.getToken{

         val request = Request.Builder()
             .addHeader("Authorization", "Bearer $it")
             .url("http://192.168.162.154:8080/api/auth/ws/1").build()
        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    _messages.value = _messages.value + text
                }
            }

            // Handle other WebSocket events like onOpen, onFailure, etc.
        }
        webSocket = client.newWebSocket(request, listener)
         }
    }

    override fun onCleared() {
        super.onCleared()
        // Close WebSocket when ViewModel is cleared
        webSocket.close(1000, "App closed")
        client.dispatcher.executorService.shutdown()
    }
    var userRole by mutableStateOf(RoleEnum.USER)
    var authsize by mutableStateOf(1)

    private val _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>> get() = _location
    init {
        // Observing changes to the location LiveData
        location.observeForever { newLocation ->
            logLocationChange(newLocation)
        }
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
                sharedViewModel._company.value.latitude = latitude
                sharedViewModel._company.value.longitude = longitude
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
      block()
    }

     fun block(){
         viewModelScope.launch {
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
        getMyUserDetails()
        userRole()
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



    fun getMyUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("getmyuserdetails","user name : inside scoupe")
                try {
                    val response = repository.getMyUserDetails()
                    if (response.isSuccessful) {
                        Log.e("getmyuserdetails","user name1 : ")
                        Log.e("getmyuserdetails","user name : ${response.body()?.username}")
                        Log.e("getmyuserdetails","user name2 : ")
                        storeUser(response.body()!!)
                        sharedViewModel._user.value = response.body()!!
                    }
                } catch (ex: Exception) {
                    Log.e("getmyuserdetails", "error is : $ex")
                }

        }
    }

     fun userRole(){
        viewModelScope.launch(Dispatchers.IO) {
            getToken {
                if (it != null) {
                    TokenUtils.isUser(it,
                        authSize = {
                            authSize ->
                           authsize = authSize
                        })
                    {isUser ->
                        when(isUser){
                            RoleEnum.ADMIN->{
                                 if (authsize == 1){
                                     sharedViewModel.accountType = AccountType.COMPANY
                                }else{
                                     sharedViewModel.accountType = AccountType.USER
                                }
                            userRole = isUser
                                getMyUserDetails()
                            getMyCompany()
                            }
                           else ->{
                            userRole = isUser
                               getMyUserDetails()


                        }
                    }
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
     fun getToken(onTokenRetrieved: (String?) -> Unit) {
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

    private fun storeUser(user: UserDto) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("storeUser", "storeUser image: ${user.image}")
            try {
                userDatastore.updateData{
                    UserDto().copy(
                        id = user.id,
                        username = user.username,
                        address = user.address,
                        phone = user.phone,
                        email = user.email,
                        image = user.image,
                        rate = user.rate,
                        rater = user.rater,
                        balance = user.balance,
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
                    storeCompany(response.body()!!)
                    Log.e("getmyuserdetails", "Error storing token store user fun in app view model:")
                    sharedViewModel._company.value = response.body()!!
                }
            }catch (ex : Exception){
                Log.e("exeptions","error is : $ex")
            }
        }
    }

     fun storeCompany(company: CompanyDto) {
        viewModelScope.launch {
            try {
                companyDataStore.updateData{
                    CompanyDto().copy(
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

    fun updateCompanyName(newName: String, onUpdated: (CompanyDto) -> Unit) {
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
//                userdatastore.updateData { currentUser ->
//                    currentUser.apply {
//                        image = newName
//                    }.also {
////                        sharedViewModel._user.value = currentUser
//                    }
//                }
                userDatastore.updateData { currentUser ->
                    currentUser.copy (
                        image = newName
                    ).also {
                        sharedViewModel._user.value = currentUser
                    }
                }
            } catch (e: Exception) {
                Log.e("storeCompanyError", "Error storing company name: ${e.message}")
            }
        }
    }

    fun updateCompanyBalance(blc : Double){
        viewModelScope.launch {
//            datastore1.updateData { currentCompany ->
//                currentCompany.apply {
//                    balance = blc
//                }.also {
////                sharedViewModel._company.value = currentCompany
//                }
//            }
            companyDataStore.updateData { currentCompany ->
                currentCompany.copy (
                    balance = blc
                ).also {
                sharedViewModel._company.value = currentCompany
                }
            }
        }
    }


    fun updateImage(file : File){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.updateImage(file)
                if(response.isSuccessful){
                    when(sharedViewModel.accountType){
                        AccountType.COMPANY ->{

                            updateCompanyName(file.name){
                        sharedViewModel._company.value = it
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


    fun logout(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                dataStore.updateData {
                    it.copy(token = "")
                }
                companyDataStore.updateData {
                    CompanyDto().copy(
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
                        user = UserDto()
                    )
                }
                userDatastore.updateData {
                    UserDto().copy(
                        id = 0,
                        username = "",
                        address = "",
                        phone = "",
                        balance = 0.0,
                        image = "",
                    )
                }
                sharedViewModel.accountType = AccountType.USER
               roomBlock()
            }
        }
    }



    fun roomBlock(){
        room.clearAllTables()
    }

}