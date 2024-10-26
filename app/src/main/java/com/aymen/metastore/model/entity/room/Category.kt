package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_werehouse",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companyId"]),
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long? = 0,
    val code: String? = null,
    val libelle: String? = null,
    val image: String? = null,
    val companyId : Long? = null
)
