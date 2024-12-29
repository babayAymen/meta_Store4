package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.CommandLine
import com.aymen.metastore.model.entity.room.remoteKeys.CommandLineByInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle

@Dao
interface CommanLineDao {

    @Upsert
    suspend fun insertCommandLine(commandLine : List<CommandLine>)

    @Upsert
    suspend fun insertCommandLineByInvoiceKeys(keys : List<CommandLineByInvoiceRemoteKeysEntity>)

    @Query("SELECT * FROM command_line_by_invoice_remote_keys_entity WHERE id = :id")
    suspend fun getCommandLineByInvoiceRemoteKey(id : Long) : CommandLineByInvoiceRemoteKeysEntity

    @Query("DELETE FROM command_line")
    suspend fun clearAllCommandLineByInvoice()

    @Query("DELETE FROM command_line_by_invoice_remote_keys_entity")
    suspend fun clearInvoiceRemoteKeysTable()

    @Transaction
    @Query("SELECT * FROM command_line WHERE invoiceId = :invoiceId")
     fun getAllCommandsLineByInvoiceId(invoiceId : Long):PagingSource<Int,CommandLineWithInvoiceAndArticle>

    @Transaction
    @Query("SELECT * FROM command_line WHERE invoiceId = :invoiceId")
    fun testroom(invoiceId : Long):List<CommandLineWithInvoiceAndArticle>

    @Query("DELETE FROM command_line WHERE id = :id")
    suspend fun deleteCommandLineById(id : Long)
}