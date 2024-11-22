package com.aymen.metastore.model.entity.dto

import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory

data class RegisterRequest(

    val id : Long? = null,

    var username: String,

    var phone: String,

    val address: String,

    var email: String,

    var password :String,

    val  longitude: Double,

    val latitude: Double,

    var category : CompanyCategory,

    var type : AccountType
)
