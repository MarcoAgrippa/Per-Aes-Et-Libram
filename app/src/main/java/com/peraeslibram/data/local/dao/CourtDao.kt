package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.CourtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtDao {

    @Query("SELECT * FROM courts ORDER BY naziv COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<CourtEntity>>

    @Query("SELECT * FROM courts WHERE id = :id")
    suspend fun getById(id: Long): CourtEntity?

    @Insert
    suspend fun insert(court: CourtEntity): Long

    @Update
    suspend fun update(court: CourtEntity)

    @Delete
    suspend fun delete(court: CourtEntity)
}
