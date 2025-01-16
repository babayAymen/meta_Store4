package com.aymen.metastore.dependencyInjection

import android.content.Context
import android.os.Build
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(
    private val context : Context
) : Interceptor {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtil.isOnline(context)) {
            // Optionally return an empty response or a fallback response
            return Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(503) // Service Unavailable
                .message("No network")
                .body("".toResponseBody(null))
                .build()
        }
        return try {
            val response = chain.proceed(chain.request())
            if(!response.isSuccessful){
                handelServerError(response)
            }
            response
        } catch (ex : IOException){
            handelIOException(ex)
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(503) // Service Unavailable
                .message("server unavailable")
                .body(ResponseBody.create(null, ""))
                .build()
        }
    }

    private fun handelServerError(response: Response) {
        when (response.code) {
            500 -> showToast("there is a problem with server")
            400 -> showToast("the server is not found")
            else -> showToast("the server is not available")
        }
    }
        private fun handelIOException(ex : IOException){
            showToast("there is a problem try again later")
        }
    private  fun showToast(message : String){
        Handler(context.mainLooper).post{
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        }
    }
}