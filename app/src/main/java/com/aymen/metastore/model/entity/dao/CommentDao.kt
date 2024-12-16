package com.aymen.metastore.model.entity.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Comment
import com.aymen.metastore.model.entity.room.remoteKeys.CommentArticleRemoteKeys

@Dao
interface CommentDao {

    @Upsert
    suspend fun insertComment(comment : List<Comment?>){
        comment.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }

    @Upsert
    suspend fun insert(comment : List<Comment>)

    @Upsert
    suspend fun insertCommentRemoteKeys(keys :  List<CommentArticleRemoteKeys>)

    @Query("SELECT * FROM comment_article_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getFirstCommentArticleRemoteKey() : CommentArticleRemoteKeys?
    @Query("SELECT * FROM comment_article_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getLatestCommentArticleRemoteKey() : CommentArticleRemoteKeys?
    @Query("DELETE FROM comment WHERE articleId = :articleId")
    suspend fun clearAllCommentArticleTableByArticleId(articleId : Long)
    @Query("DELETE FROM comment_article_remote_keys WHERE articleId = :articleId")
    suspend fun clearAllRemoteKeysTableByArticleId(articleId : Long)
}