package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "points_payment_per_day_remote_keys")
data class PointsPaymentPerDayRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val nextPage: Int?,
    val prevPage: Int?
)
