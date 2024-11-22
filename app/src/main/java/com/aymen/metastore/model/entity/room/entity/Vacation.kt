package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import io.realm.kotlin.types.annotations.PrimaryKey
@Entity(tableName = "vacation",
    foreignKeys = [
        ForeignKey(entity = Worker::class, parentColumns = ["id"], childColumns = ["workerId"])
    ])
data class Vacation(

    @PrimaryKey
    val id : Long ? = null,
    val year : Int = 0,
    val startdate: String? = null,
    val enddate: String? = null,
    val workerId: Long? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
    val companyId : Long? = null
){
    fun toVacation(worker: com.aymen.metastore.model.entity.model.Worker,
                   company: com.aymen.metastore.model.entity.model.Company
    ):com.aymen.metastore.model.entity.model.Vacation{
        return com.aymen.metastore.model.entity.model.Vacation(
            id = id,
            year = year,
            startdate = startdate,
            enddate = enddate,
            worker = worker,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            company = company
        )
    }
}
