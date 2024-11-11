package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient

@Dao
interface CompanyDao {

    @Upsert
    suspend fun insertCompany(company: Company)

    @Query("SELECT userId FROM company WHERE id = :companyId")
    suspend fun getUserIdByCompanyId(companyId : Long) : Long

    @Query("SELECT * FROM company WHERE id = :companyId")
    suspend fun getCompanyById(companyId: Long) : Company

    @Query("SELECT * FROM company WHERE (name LIKE '%' || :search || '%' OR code LIKE '%' || :search || '%')")
    suspend fun getAllCompaniesContaining(search : String) : List<Company>

}