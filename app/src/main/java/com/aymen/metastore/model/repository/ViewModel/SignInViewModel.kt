package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val dataStore: DataStore<AuthenticationResponse>,
    private val appViewModel: AppViewModel
): ViewModel() {



    fun signIn(authenticationRequest: AuthenticationRequest, onSignInSuccess: (Boolean) -> Unit){
        viewModelScope.launch (Dispatchers.IO){
                try {
                    val token = repository.SignIn(authenticationRequest)
                    if (token.isSuccessful) {
                        appViewModel.block()
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val token = repository.SignUp(registerRequest)
                    if (token.isSuccessful) {
                        storeToken(token.body()!!)
                        onSignUpSuccess(true)
                    } else {
                        onSignUpSuccess(false)
                    }
                } catch (_: Exception) {

                }
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


private fun storeToken(token: AuthenticationResponse) {
    viewModelScope.launch(Dispatchers.Main) {
            try {
                dataStore.updateData {
                    it.copy(token = token.token)
                }
                appViewModel.block()
            } catch (e: Exception) {
                Log.e("storeTokenError", "Error storing token in signin view model store token fun: ${e.message}")
            }
    }
}




}