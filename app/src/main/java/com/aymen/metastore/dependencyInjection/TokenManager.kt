package com.aymen.metastore.dependencyInjection

import androidx.datastore.core.DataStore
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager  @Inject constructor(
    private val dataStore: DataStore<AuthenticationResponse>
) {

    val tokenFlow: Flow<String?> = dataStore.data.map { authResponse ->
        authResponse.token
    }

    suspend fun saveToken(newToken: String) {
        dataStore.updateData { current ->
            current.copy(token = newToken) // Update the token in DataStore
        }
    }

    suspend fun getToken(): String? {
        return tokenFlow.firstOrNull() // Get token synchronously for interceptors
    }

    suspend fun clearToken() {
        dataStore.updateData {
            it.copy(token = "")
        }
    }
}