package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting

@Entity(tableName = "company",
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["parentCompanyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ],
    indices = [Index( "userId" ,unique = true), Index(value = ["parentCompanyId"], unique = true), Index(value = ["companyId"])]
)
data class Company(

    @PrimaryKey(autoGenerate = false) val companyId: Long? = null,
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
){
    fun toCompany(user : com.aymen.metastore.model.entity.model.User?,
                  parent : com.aymen.metastore.model.entity.model.Company?
    ): com.aymen.metastore.model.entity.model.Company{
        return com.aymen.metastore.model.entity.model.Company(
            companyId,
            name,
            code,
            matfisc,
            address,
            phone,
            bankaccountnumber,
            email,
            capital,
            logo,
            workForce,
            virtual,
            rate,
            raters,
            user = user,
            parentCompany = parent,
            category,
            balance,
            isPointsSeller,
            metaSeller,
            longitude,
            latitude,
            isVisible,
            createdDate,
            lastModifiedDate,
            invoiceType


        )

    }
}
