package com.aymen.metastore.model.repository.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.dependencyInjection.TokenManager
import com.aymen.metastore.model.ViewModelRunTracker
import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.dependencyInjection.TokenUtils
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val dataStore: DataStore<AuthenticationResponse>,
    private val appViewModel: AppViewModel,
    private val companyDataStore: DataStore<Company>,
    private val userDatastore: DataStore<User>,
    private val sharedViewModel: SharedViewModel,
    private val accountTypeDataStore: DataStore<AccountType>,
    private val room : AppDatabase,
    private val tokenManager: TokenManager
): ViewModel() {

    fun signIn(authenticationRequest: AuthenticationRequest, onSignInSuccess: (Boolean) -> Unit){
        viewModelScope.launch (Dispatchers.IO){
                try {
                    val token = repository.SignIn(authenticationRequest)
                    Log.e("token","token : ${token.body()}")
                    if (token.isSuccessful) {
                            tokenManager.saveToken(token.body()?.token!!) // Save new token dynamically
                        getUserRole(token.body()?.token!!)
                        storeToken(token.body()!!)
                        onSignInSuccess(true)
                    } else {
                        onSignInSuccess(false)
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabaysignIn","clicked exeption : $_ex")
                }

        }

    }
    fun signUp(registerRequest: RegisterRequest, onSignUpSuccess: (Boolean) -> Unit){
        viewModelScope.launch(Dispatchers.IO)  {
                try {
                    val token = repository.SignUp(registerRequest)
                    if (token.isSuccessful) {
                        tokenManager.saveToken(token.body()?.token!!)
                        getUserRole(token.body()?.token!!)
                        storeToken(token.body()!!)
                        onSignUpSuccess(true)
                    } else {
                        onSignUpSuccess(false)
                    }
                } catch (_: Exception) {

                }
        }
    }


    var userRole by mutableStateOf(RoleEnum.USER)

    private fun getUserRole(token : String){

            TokenUtils.isUser(token)
            { isUser ->
                        Log.e("token","user role is $isUser")
                when (isUser) {
                    RoleEnum.USER ->{
                        getMyUserDetails()
                    }
                    else -> {
                            sharedViewModel.assignAccountType(AccountType.COMPANY)
                            storeAccountType(AccountType.COMPANY)
                        getMyCompany()
                    }
                }

                userRole = isUser
        }
    }

    private val _showCheckLocationDialog = MutableStateFlow(false)
    val showCheckLocationDialog: StateFlow<Boolean> = _showCheckLocationDialog

    @SuppressLint("SuspiciousIndentation")
    fun getMyCompany(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(userRole == RoleEnum.WORKER){
                getMyUserDetails()
            }
                val response = repository.getMeAsCompany()
                Log.e("token","company size : ${response.body()?.id}")
                if(response.isSuccessful){
                    Log.e("userrole","user role in get company 1 $userRole")
                    val company = response.body()!!
                   checkLocation(company.toCompanyModel(), null){
                       _showCheckLocationDialog.value = it
                   }
                    Log.e("userrole","user role in get company 2 $userRole")
                    storeCompany(company.toCompanyModel())
                    Log.e("userrole","user role in get company 3 $userRole")
                    if(userRole == RoleEnum.ADMIN) {
                        Log.e("userrole","user role in get company 4 ${company.user}")
                        storeUser(company.user?.toUserModel()!!)
                        sharedViewModel.assignUser(company.user.toUserModel())
                    }
                    sharedViewModel.assignCompanyy( company.toCompanyModel())
                    room.companyDao().insertCompany(listOf(company.toCompany()))

                }
            }catch (ex : Exception){
                Log.e("exeptions","error is : $ex")
            }
        }
    }

    fun checkLocation(company : Company? , user : User?, isLocation : (Boolean) -> Unit){
        isLocation(company?.latitude == 0.0 || company?.longitude == 0.0)
        isLocation(user?.latitude == 0.0 || user?.longitude == 0.0)
    }

    private fun getMyUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMyUserDetails()
                if (response.isSuccessful) {
                    val user = response.body()?.toUserModel()
                    sharedViewModel.assignAccountType(user?.accountType!!)
                    storeAccountType(user.accountType)
                    checkLocation(null, user){
                        _showCheckLocationDialog.value = it
                    }
                    storeUser(user)
                    sharedViewModel.assignUser(user)
                    room.userDao().insertUser(listOf(user.toUserEntity()))
                }
            } catch (ex: Exception) {
                Log.e("getmyuserdetails", "error is : $ex")
            }

        }
    }


private fun storeToken(token: AuthenticationResponse) {
    viewModelScope.launch(Dispatchers.Main) {
            try {
                dataStore.updateData {
                    it.copy(token = token.token)
                }
            } catch (e: Exception) {
                Log.e("storeTokenError", "Error storing token in signin view model store token fun: ${e.message}")
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
                        latitude = user.latitude,
                        role = user.role,
                        accountType = user.accountType
                    )
                }
            } catch (e: Exception) {
                Log.e("storeUserError", "Error storing token store user fun in app view model: ${e.message}")
            }
        }
    }


    fun storeCompany(company: Company) {
        viewModelScope.launch {
            Log.e("storeCompany", "storeCompany image: ${company.id}")
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


}