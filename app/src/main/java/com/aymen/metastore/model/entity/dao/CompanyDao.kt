package com.aymen.metastore.model.entity.dao

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient

@Dao
interface CompanyDao {

    @Upsert
    suspend fun insert(company : List<Company>)

    suspend fun insertCompany(company: List<Company?>) {
        company.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                Log.e("error","company before saving : $it")
                insert(it)
            }
    }

    @Query("SELECT * FROM company")
     fun getAllMyClient() : List<Company>


    @Query("SELECT userId FROM company WHERE companyId = :companyId")
    suspend fun getUserIdByCompanyId(companyId : Long) : Long

    @Query("SELECT * FROM company WHERE companyId = :companyId")
    suspend fun getCompanyById(companyId: Long) : Company

    @Query("UPDATE company SET rate = :rateValue WHERE companyId = :id")
    suspend fun updateCompany(id : Long , rateValue : Double)
    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE createdDate = :search")// select from company where (name LIKE '%' || :search || '%' OR code LIKE '%' || :search || '%')
     fun getAllCompaniesContaining(search : String) : PagingSource<Int,CompanyWithCompanyClient>

     @Query("SELECT MAX(companyId) FROM company")
     suspend fun getLatestCompanyId() : Long?

     @Query("SELECT COUNT(*) FROM company ")
     suspend fun getCompanyCount() : Int

     @Upsert
     suspend fun insertSingleCompany(company : Company)

     @Query("DELETE FROM company WHERE companyId = :id")
     suspend fun deleteCompanyById(id : Long)

}