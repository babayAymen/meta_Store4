package com.aymen.store.dependencyInjection

import android.util.Log
import com.auth0.android.jwt.JWT
import com.aymen.store.model.Enum.RoleEnum

object TokenUtils {

    fun isUser(token: String, authSize:(Int) -> Unit, userRole:(RoleEnum) -> Unit) {
        val jwt = JWT(token)
        val authList = jwt.getClaim("Authorization").asList(Map::class.java)
        authSize(authList.size)
        authList?.forEach { auth ->
            when (val authority = auth["authority"]) {
//                    "USER" ->{
//                    Log.e("aymenbabayrole", "authority user $authority")
//                        userRole(RoleEnum.USER)
//                    }
                "ADMIN" ->{
                    Log.e("aymenbabayrole", "authority admin $authority")
                    userRole(RoleEnum.ADMIN)
                }
                "SELLER" ->{
                    Log.e("aymenbabayrole", "authority seller $authority")
                    userRole(RoleEnum.SELLER)
                }
                "AYMEN" ->{
                    Log.e("aymenbabayrole", "authority aymen $authority")
                    userRole(RoleEnum.AYMEN)
                }
                "WORKER" ->{
                    Log.e("aymenbabayrole", "authority worker $authority")
                    userRole(RoleEnum.WORKER)
                }
                else ->{
                    userRole(RoleEnum.USER)
                }
            }
        }

    }

    fun isExpired(token: String): Boolean {
        val jwt = JWT(token)
        Log.e("jwt", jwt.toString())
        Log.e("jwt token", token)
        Log.d("jwt", jwt.isExpired(0).toString())
        return jwt.isExpired(0)
    }

    fun UserName(token: String, userName:(String) -> Unit){
        val jwt = JWT(token)
        userName(jwt.getClaim("sub").asString()!!)
    }
}