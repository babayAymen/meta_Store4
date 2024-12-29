package com.aymen.metastore.dependencyInjection

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(
    private val context : Context
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtil.isOnline(context)) {
            // Optionally return an empty response or a fallback response
            return Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(503) // Service Unavailable
                .message("No network")
                .body(ResponseBody.create(null, ""))
                .build()
        }
        return chain.proceed(chain.request())
    }
}