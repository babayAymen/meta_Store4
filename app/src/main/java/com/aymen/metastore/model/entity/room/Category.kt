package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_werehouse"
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long? = 0,
    val code: String,
    val libelle: String,
    val image: String? = null,
)
