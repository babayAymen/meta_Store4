package com.aymen.metastore.model.entity.model

class Comment (

     val id : Long? = null,
     val content: String = "",
     val user: User? = null,
     val company: Company? = null,
     val article: ArticleCompany? = null,
     val createdDate : String = "",
     val lastModifiedDate : String = "",
)