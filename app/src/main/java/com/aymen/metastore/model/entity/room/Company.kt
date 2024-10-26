package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting

@Entity(tableName = "company",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["parentCompanyId"]),
    ]
)
data class Company(

    @PrimaryKey(autoGenerate = false) val id: Long? = null,

    val name: String = "",
    val code: String? = "",
    val matfisc: String? = "",
    val address: String? = "",
    val phone: String? = "",
    val bankaccountnumber: String? = "",
    val email: String? = "",
    val capital: String? = "",
    val logo: String? = "",
    val workForce: Int? = 0,
    val virtual : Boolean? = false,
    val rate: Double? = 0.0,
    val raters: Int? = 0,
    val userId: Long? = null,
    val parentCompanyId: Long? = null,
    val category : CompanyCategory? = CompanyCategory.DAIRY,
    val balance : Double? = 0.0,
    val isPointsSeller : Boolean? = false,
    val metaSeller : Boolean? = false,
    val longitude : Double? = 0.0,
    val latitude : Double? = 0.0,
    val isVisible: PrivacySetting? = PrivacySetting.PUBLIC,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
    val invoiceType : InvoiceType? = InvoiceType.NOT_SAVED
)
