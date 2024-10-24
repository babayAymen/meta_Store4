package com.aymen.metastore.model.entity.Dao

import com.aymen.metastore.model.entity.room.User
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {


    @Upsert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>
}