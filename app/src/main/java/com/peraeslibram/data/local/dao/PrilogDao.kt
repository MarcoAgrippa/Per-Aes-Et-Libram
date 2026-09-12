package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.PrilogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrilogDao {

    @Query("SELECT * FROM prilozi WHERE caseId = :caseId ORDER BY datumKreiranja ASC")
    fun observeByCase(caseId: Long): Flow<List<PrilogEntity>>

    @Query("SELECT * FROM prilozi WHERE id = :id")
    suspend fun getById(id: Long): PrilogEntity?

    @Insert
    suspend fun insert(prilog: PrilogEntity): Long

    @Update
    suspend fun update(prilog: PrilogEntity)

    @Delete
    suspend fun delete(prilog: PrilogEntity)
}
