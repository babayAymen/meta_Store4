package com.aymen.metastore.model.entity.Dao

import com.aymen.metastore.model.entity.room.User
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {


    @Upsert
    suspend fun insertUser(user: User)

    @Query("SELECT username FROM user WHERE id = :userId")
    suspend fun getUserNameById(userId : Long): String

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId : Long): User
}