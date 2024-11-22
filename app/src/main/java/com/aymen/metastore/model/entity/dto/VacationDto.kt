package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Vacation
import java.util.Date

data class VacationDto(

    val id : Long? = null,
    var year : Int,
    var startdate : Date,
    var enddate : Date,
    var worker : WorkerDto,
    var company : CompanyDto
){
    fun toVacation() : Vacation {

        return Vacation(
            id = id,
            year = year,
            startdate = startdate.toString(),
            enddate = enddate.toString(),
            workerId = worker.id,
            companyId = company.id,
        )
    }
}
