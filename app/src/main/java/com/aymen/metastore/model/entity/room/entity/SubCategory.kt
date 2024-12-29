package com.aymen.metastore.model.entity.room.entity
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
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class SubCategory(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var code: String? = null,
    var libelle: String? = null,
    val image: String? = null,
    val categoryId: Long? = null,
    val companyId: Long? = null,
    val isSubcategory : Boolean? = false
){
    fun toSubCategory(category : com.aymen.metastore.model.entity.model.Category,
                      company : com.aymen.metastore.model.entity.model.Company): com.aymen.metastore.model.entity.model.SubCategory {
        return com.aymen.metastore.model.entity.model.SubCategory(
            id = id,
            code = code,
            libelle = libelle,
            image = image,
            category = category,
            company = company
        )
    }
}

