package com.aymen.store.dependencyInjection

import android.util.Log
import com.auth0.android.jwt.JWT
import com.aymen.store.model.Enum.RoleEnum

object TokenUtils {

    fun isUser(token: String, userRole:(RoleEnum) -> Unit) {
        val jwt = JWT(token)
        val authList = jwt.getClaim("Authorization").asList(Map::class.java)
        authList?.forEach { auth ->
            when (val authority = auth["authority"]) {
                RoleEnum.ADMIN.toString() ->{
                    Log.e("aymenbabayrole", "authority admin $authority")
                    userRole(RoleEnum.ADMIN)
                    return
                }
                RoleEnum.PARENT.toString() ->{
                    Log.e("aymenbabayrole", "authority seller $authority")
                    userRole(RoleEnum.PARENT)
                }
                RoleEnum.WORKER.toString() ->{
                    Log.e("aymenbabayrole", "authority worker $authority")
                    userRole(RoleEnum.WORKER)
                }
                else ->{
                    Log.e("aymenbabayrole", "authority user $authority")
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