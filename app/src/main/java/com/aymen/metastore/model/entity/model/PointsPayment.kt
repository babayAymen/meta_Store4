package com.aymen.metastore.model.entity.model

data class PointsPayment(
    var id: Long? = null,
    var amount: Long? = 0,
    var provider: Company? = null,
    var clientCompany: Company? = null,
    var clientUser: User? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = ""
)