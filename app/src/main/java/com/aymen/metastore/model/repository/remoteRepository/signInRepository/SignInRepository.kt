package com.aymen.store.model.repository.remoteRepository.signInRepository

import com.aymen.store.model.entity.dto.AuthenticationRequest
import com.aymen.store.model.entity.dto.AuthenticationResponse
import com.aymen.store.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.realm.User
import retrofit2.Response

interface SignInRepository {

    suspend  fun SignIn(authenticationRequest: AuthenticationRequest): Response<AuthenticationResponse>

    suspend  fun SignUp(registerRequest: RegisterRequest): Response<AuthenticationResponse>

    suspend fun refreshToken(token : String) : Response<AuthenticationResponse>

    suspend fun getMyUserDetails(): Response<User>

    suspend fun updateLocations(latitude : Double , logitude : Double):Response<Void>
}