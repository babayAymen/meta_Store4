package com.aymen.store.model.repository.remoteRepository.signInRepository

import com.aymen.store.model.entity.api.AuthenticationRequest
import com.aymen.store.model.entity.api.RegisterRequest
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.log

class SignInRepositoryImpl @Inject constructor
    (private val api: ServiceApi)
    :SignInRepository{
    override suspend fun SignIn(authenticationRequest: AuthenticationRequest) = api.SignIn(authenticationRequest)

    override suspend fun SignUp(registerRequest: RegisterRequest) = api.SignUp(registerRequest)
    override suspend fun refreshToken(token: String) = api.refreshToken(token)
    override suspend fun getMyUserDetails() = api.getMyUserDetails()
    override suspend fun updateLocations(latitude: Double, logitude: Double) = api.updateLocations(latitude, logitude)

}