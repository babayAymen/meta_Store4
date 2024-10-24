package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.CommandLine

@Dao
interface CommanLineDao {

    @Upsert
    suspend fun insertCommandLine(commandLine : CommandLine)

    @Query("SELECT * FROM command_line")
    suspend fun getAllCommandsLine():List<CommandLine>
}