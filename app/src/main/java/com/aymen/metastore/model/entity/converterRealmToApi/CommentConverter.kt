package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.CommentDto
import com.aymen.metastore.model.entity.room.Comment

fun mapCommentToRoomComment(comment : CommentDto):Comment{
    return Comment(
         id = comment.id,

     content = comment.content,

     userId = comment.user?.id,

     companyId = comment.company?.id,

     articleId = comment.article?.id,

     createdDate = comment.createdDate,

     lastModifiedDate = comment.lastModifiedDate,
    )
}