package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Company

@Dao
interface CompanyDao {

    @Upsert
    suspend fun insertCompany(company: Company)

    @Query("SELECT * FROM company")
    suspend fun getAllCompanies(): List<Company>

    @Query("SELECT userId FROM company WHERE id = :companyId")
    suspend fun getUserIdByCompanyId(companyId : Long) : Long
}