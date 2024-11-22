package com.aymen.metastore.model.entity.Dao

import android.util.Log
import com.aymen.metastore.model.entity.room.entity.User
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {


    @Upsert
    suspend fun insert(user : List<User>)

    suspend fun insertUser(user: List<User?>) {
        user.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                Log.e("error","from dao $it")
                insert(it)
            }
    }



    @Query("SELECT username FROM user WHERE id = :userId")
    suspend fun getUserNameById(userId : Long): String

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId : Long): User
}