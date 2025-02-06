package com.aymen.store.model.repository.remoteRepository.signInRepository

import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
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
    override suspend fun sendVerificationCodeViaEmail(
        username: String,
        email: String
    )= api.sendVerificationCodeViaEmail(username,email)

    override suspend fun verificationCode(
        username: String,
        email: String,
        code: String
    ) = api.verificationCode(username,email, code)

    override suspend fun changePassword(
        username: String,
        email: String,
        password: String
    ): Response<AuthenticationResponse> = api.changePassword(username,email, password)

}