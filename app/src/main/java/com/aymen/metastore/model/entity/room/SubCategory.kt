package com.aymen.metastore.model.entity.room
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subcategory_werehouse",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class SubCategory(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val code: String,
    val libelle: String,
    val image: String? = null,
    val categoryId: Long? = null,
)

