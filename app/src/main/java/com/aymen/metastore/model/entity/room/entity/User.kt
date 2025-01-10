package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum

@Entity(tableName = "user",
    indices = [Index(value = ["id"])]
)
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
    val image : String? = null,
    val rate: Double? = 0.0,
    val rater: Int? = 0,
    val accountType: AccountType? = AccountType.NULL
){
    fun toUser(): User{
        return User(
            id= id,
            phone = phone,
            address = address,
            username = username,
            email = email,
            resettoken = resettoken,
            longitude = longitude,
            latitude = latitude,
            role = role,
            balance = balance,
            image = image,
            rate = rate,
            rater = rater,
            accountType = accountType
        )
    }
}
