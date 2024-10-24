package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.RoleEnum

@Entity(tableName = "user")
data class User(

    @PrimaryKey(autoGenerate = false) val id: Long? = null,
    val phone : String? = null,
    val address : String? = null,
    val username : String? = null,
    val email : String? = "",
    val resettoken : String? = "",
    val longitude : Double? = 0.0,
    val latitude : Double? = 0.0,
    val role : RoleEnum? = RoleEnum.USER,
    val balance : Double? = 0.0,
    val image : String? = ""
)
