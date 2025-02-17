package com.aymen.store.model.repository.remoteRepository.signInRepository

import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.dto.TokenDto
import com.aymen.metastore.model.entity.dto.UserDto
import retrofit2.Response

interface SignInRepository {

    suspend  fun SignIn(authenticationRequest: AuthenticationRequest): Response<AuthenticationResponse>
    suspend  fun SignUp(registerRequest: RegisterRequest): Response<AuthenticationResponse>
    suspend fun refreshToken(token : String) : Response<AuthenticationResponse>
    suspend fun sendMyDeviceToken(token : TokenDto): Response<Void>
    suspend fun getMyUserDetails(): Response<UserDto>
     suspend fun updateLocations(latitude : Double , logitude : Double):Response<Void>
     suspend fun sendVerificationCodeViaEmail(username : String , email : String) : Response<Boolean>
     suspend fun verificationCode(username : String , email : String, code : String) : Response<Boolean>
     suspend fun changePassword(username : String , email : String, password : String) : Response<AuthenticationResponse>
}