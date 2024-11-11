package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Rating
import com.aymen.metastore.model.entity.roomRelation.RatingWithRater

@Dao
interface RatingDao {

    @Upsert
    suspend fun insertRating(rating : Rating)

    @Query("SELECT * FROM rating WHERE rateeCompanyId = :id")
    suspend fun getAllCompanyRating(id : Long) : List<RatingWithRater>

    @Query("SELECT * FROM rating WHERE rateeUserId = :id")
    suspend fun getAllUserRating(id : Long) : List<RatingWithRater>
}