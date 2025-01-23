package com.aymen.metastore.model.repository.ViewModel

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
import com.aymen.metastore.model.Location.LocationService
import com.aymen.metastore.model.ViewModelRunTracker
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.TokenDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val dataStore: DataStore<AuthenticationResponse>,
    private val companyDataStore: DataStore<Company>,
    private val userDatastore: DataStore<User>,
    private val sharedViewModel: SharedViewModel,
    private val context: Context,
    private val accountTypeDataStore: DataStore<AccountType>,
    private val tracker: ViewModelRunTracker
) : ViewModel(){

    val isFirstRun: Boolean


    private var _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user

    private val _currentScreen = mutableStateOf(IconType.HOME)
    val currentScreen: State<IconType> get() = _currentScreen
    var _historySelected = mutableStateOf(currentScreen.value) // Match the type here
    val historySelected: State<IconType> get() = _historySelected

    var userRole by mutableStateOf(sharedViewModel.user.value.role)
    var asClient by mutableStateOf(false)
    private val _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>> get() = _location
    init {  isFirstRun = tracker.isFirstViewModelRun
        if (isFirstRun) {
            tracker.firstViewModelName = "app view"
            tracker.isFirstViewModelRun = false
            // Perform first-run initialization
        }
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
                AccountType.META -> {}
                AccountType.NULL -> {}
                AccountType.SELLER -> {}
                AccountType.DELIVERY -> TODO()
            }
        }
        }
    }

    private val _showCheckLocationDialog = MutableStateFlow(false)
    val showCheckLocationDialog: StateFlow<Boolean> = _showCheckLocationDialog


    fun updateScreen(newValue: IconType) {
        _historySelected.value = currentScreen.value
        _currentScreen.value = newValue
    }
    init {
        viewModelScope.launch {
            accountTypeDataStore.data.collect{item ->
                    sharedViewModel.assignAccountType( item)
                    when(item){
                        AccountType.COMPANY -> {
                            launch {
                                companyDataStore.data.collect{
                                    if(it.longitude == 0.0 || it.latitude == 0.0 ){
                                        _showCheckLocationDialog.value = true
                                    }
                                }
                            }
                        }
                        AccountType.USER -> {
                            launch {
                                userDatastore.data.collect {
                                    if (it.longitude == 0.0 || it.latitude == 0.0) {
                                        _showCheckLocationDialog.value = true
                                    }
                                }
                            }
                        }
                        AccountType.META -> {}
                        AccountType.SELLER -> {}
                        AccountType.DELIVERY -> {}
                        AccountType.NULL -> {}
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
    fun updateView(newValue: String, history : String? = null){
        if(history != null)
            _historicView.value = history
        else
            _historicView.value = view.value
        _view.value = newValue
        Log.e("viewmodelldn","new value : $newValue history : $history historicvalue : ${_historicView.value} and view current : ${view.value}")
    }

    private val _historicView = mutableStateOf(view.value)
    val historicView: State<String> get() = _historicView



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


    fun updateCompanyName(newName: String, onUpdated: (Company) -> Unit) {
        viewModelScope.launch {
            try {
                companyDataStore.updateData { currentCompany ->
                    currentCompany.copy(logo = newName)
                }
                val updatedCompany = companyDataStore.data.firstOrNull()
                updatedCompany?.let { onUpdated(it) }
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
                }
            } catch (e: Exception) {
                Log.e("storeCompanyError", "Error storing company name: ${e.message}")
            }
        }
    }

    fun updateCompanyBalance(blc : Double){
        viewModelScope.launch {
            companyDataStore.updateData { currentCompany ->
                sharedViewModel.assignCompanyy( currentCompany.copy (
                    balance = blc
                ) )
                currentCompany.copy (
                    balance = blc
                )
            }
        }
    }
    fun addCompanyBalance(blc : Double){
        viewModelScope.launch {
            companyDataStore.updateData { currentCompany ->
                val newBalance = BigDecimal(currentCompany.balance!!).add(BigDecimal(blc)).setScale(2,RoundingMode.HALF_UP)
            sharedViewModel.assignCompanyy( currentCompany.copy (
                balance = newBalance.toDouble()
            ) )
                currentCompany.copy (
                    balance = newBalance.toDouble()
                )
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
                                sharedViewModel.assignCompanyy(it)
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

    fun refreshToken( onRefreshTokenSuccess: (Boolean) -> Unit){
        var token : String = ""
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    dataStore.data
                        .catch { exception ->
                            Log.e("getTokenError", "Error getting token: ${exception.message}")
                            onRefreshTokenSuccess(false)
                        }
                        .collect { authenticationResponse ->
                            Log.e("getToken", "Token: ${authenticationResponse.token}")
                            if(authenticationResponse.token != ""){
                                repository.refreshToken(authenticationResponse.token)
                                onRefreshTokenSuccess(true)
                            }else{
                                onRefreshTokenSuccess(false)
                            }
                        }
                }catch (_ex : Exception){
                    Log.e("refreshTokenError", "Error refreshing token: ${_ex.message}")
                }
            }
        }
    }


    fun sendDeviceToken(tok : String){
        Log.e("devicetoken","token is : $tok")
        val token = TokenDto(tok)
        viewModelScope.launch(Dispatchers.IO) {
        repository.sendMyDeviceToken(token)
        }
    }



}