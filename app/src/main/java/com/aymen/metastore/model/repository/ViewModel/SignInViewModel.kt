package com.aymen.metastore.model.repository.ViewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialResponse
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.dependencyInjection.TokenManager
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
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.concurrent.TimeUnit
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

    private val auth = Firebase.auth

    private lateinit var authe: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

//    fun startGoogleSignIn(context: Context) {
//        authe = FirebaseAuth.getInstance()
//        credentialManager = CredentialManager.create(context as Activity)
//
//        // Build the GoogleIdTokenCredential
//        val googleIdTokenCredential = GoogleIdTokenCredential(
//            idToken =  default_web_client_id // Use your Firebase Web Client ID
//        )
//
//        // Build the GetCredentialRequest
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdTokenCredential)
//            .build()
//
//        // Launch the sign-in flow
//        credentialManager.getCredential(
//            request = request,
//            activity = context,
//            onSuccess = { response ->
//                handleSignIn(response, context)
//            },
//            onFailure = { e ->
//                // Handle error
//                e.printStackTrace()
//            }
//        )
//    }
    private fun handleSignIn(response: GetCredentialResponse, context: Context) {
        val credential = response.credential
        if (credential is GoogleIdTokenCredential) {
            val idToken = credential.idToken
            firebaseAuthWithGoogle(idToken, context)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, context: Context) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign-in success, get FCM token
                    getFCMToken()
                } else {
                    // Sign-in failed
                }
            }
    }
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Send token to your backend or handle it as needed
                println("FCM Token: $token")
            }
        }
    }
    fun signInWithPhoneNumber(phoneNumber : String, context: Context){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("veriffailed","error : $e")

                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid request
                        }

                        is FirebaseTooManyRequestsException -> {
                            // The SMS quota for the project has been exceeded
                        }

                        is FirebaseAuthMissingActivityForRecaptchaException -> {
                            // reCAPTCHA verification attempted with null Activity
                        }
                    }
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("veriffailed", "onCodeSent:$verificationId")
                    super.onCodeSent(verificationId, token)
                }

            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){

                }else{

                }
            }
    }

    fun signIn(authenticationRequest: AuthenticationRequest, onSignInSuccess: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result : Result<Response<AuthenticationResponse>> = runCatching {
                repository.SignIn(authenticationRequest)
            }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful){
                        val response = success.body()
                        if(response != null){
                            subSignIn(response)
                            onSignInSuccess(true)
                        }
                    }else
                        onSignInSuccess(false)
                },
                onFailure = {failure ->

                }
            )
        }

    }
    private fun subSignIn(response : AuthenticationResponse){
        viewModelScope.launch {
        tokenManager.saveToken(response.token)
        getUserRole(response.token)
        storeToken(response)
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
                if(response.isSuccessful){
                    val company = response.body()!!
                   checkLocation(company.toCompanyModel(), null){
                       _showCheckLocationDialog.value = it
                   }
                    storeCompany(company.toCompanyModel())
                    if(userRole == RoleEnum.ADMIN) {
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
                    Log.e("userdelevery","user: $user")
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

    fun sendVerificationCodeViaEmail(username : String, email : String, isVerified : (Boolean) -> Unit){
        viewModelScope.launch {
        val result : Result<Response<Boolean>> = runCatching {
            repository.sendVerificationCodeViaEmail(username , email)
        }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful) {
                        val response = success.body()
                        if (response != null)
                            isVerified(response)
                    }
                },
                onFailure = {failure ->

                }
            )
        }
    }
    fun verificationCode(username : String, email : String, code : String, isVerified : (Boolean) -> Unit){
        viewModelScope.launch {
        val result : Result<Response<Boolean>> = runCatching {
            repository.verificationCode(username , email, code)
        }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful) {
                        val response = success.body()
                        if (response != null)
                            isVerified(response)
                    }
                },
                onFailure = {failure ->

                }
            )
        }
    }
    fun changePassword(username : String, email : String, password : String, isVerified : (Boolean) -> Unit){
        viewModelScope.launch {
        val result : Result<Response<AuthenticationResponse>> = runCatching {
            repository.changePassword(username , email, password)
        }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful) {
                        val response = success.body()
                        if (response != null){
                            subSignIn(response)
                            isVerified(true)
                        }
                    }else
                        isVerified(false)
                },
                onFailure = {failure ->

                }
            )
        }
    }

}