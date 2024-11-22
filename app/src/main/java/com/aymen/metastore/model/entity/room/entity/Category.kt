package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_werehouse",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ],
    indices = [Index("companyId"), Index("id")]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long? = 0,
    val code: String? = null,
    val libelle: String? = null,
    val image: String? = null,
    val companyId : Long? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = "",
){
    fun toCategory(company: com.aymen.metastore.model.entity.model.Company): com.aymen.metastore.model.entity.model.Category {
        return com.aymen.metastore.model.entity.model.Category(
            id = id,
            code = code,
            libelle = libelle,
            image = image,
            company = company,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
