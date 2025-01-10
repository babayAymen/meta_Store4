package com.aymen.store.model.repository.remoteRepository.signInRepository

import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.dto.TokenDto
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor
    (private val api: ServiceApi)
    :SignInRepository{
    override suspend fun SignIn(authenticationRequest: AuthenticationRequest) = api.signIn(authenticationRequest)

    override suspend fun SignUp(registerRequest: RegisterRequest) = api.signUp(registerRequest)
    override suspend fun refreshToken(token: String) = api.refreshToken(token)
    override suspend fun sendMyDeviceToken(token: TokenDto) = api.sendMyDeviceToken(token)

    override suspend fun getMyUserDetails() = api.getMyUserDetails()
    override suspend fun updateLocations(latitude: Double, logitude: Double) = api.updateLocations(latitude, logitude)

}