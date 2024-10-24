package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.room.Worker
import com.aymen.store.model.entity.dto.WorkerDto

fun mapWorkerToRoomWorker(worker : WorkerDto?):Worker {
//    if (worker != null) {
        return Worker(
            id = worker?.id,
            name = worker?.name,
            phone = worker?.phone,
            email = worker?.email,
            address = worker?.address,
            salary = worker?.salary,
            jobtitle = worker?.jobtitle,
            department = worker?.department,
            totdayvacation = worker?.totdayvacation,
            remainingday = worker?.remainingday,
            statusvacation = worker?.statusvacation,
            userId = worker?.user?.id,
            companyId = worker?.company?.id,
            createdDate = worker?.createdDate!!,
            lastModifiedDate = worker.lastModifiedDate
        )

}