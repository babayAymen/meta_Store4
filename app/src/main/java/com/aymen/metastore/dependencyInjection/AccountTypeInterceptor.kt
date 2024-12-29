package com.aymen.metastore.dependencyInjection

import android.content.Context
import android.widget.Toast
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AccountTypeInterceptor @Inject constructor(
    private val sharedViewModel: SharedViewModel
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val requestBuilder = chain.request().newBuilder()

        // Fetch the latest AccountType from the ViewModel
        val currentAccountType = runBlocking(Dispatchers.Main) {
            sharedViewModel.accountType.value
        }

        // Add the Account-Type header
        requestBuilder.addHeader("Account-Type", currentAccountType.name)

        return chain.proceed(requestBuilder.build())
    }
}