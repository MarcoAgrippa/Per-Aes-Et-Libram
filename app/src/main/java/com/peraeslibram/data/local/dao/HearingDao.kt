package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.HearingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HearingDao {

    @Query("SELECT * FROM hearings ORDER BY datumVreme ASC")
    fun observeAll(): Flow<List<HearingEntity>>

    @Query("SELECT * FROM hearings WHERE caseId = :caseId ORDER BY datumVreme ASC")
    fun observeByCase(caseId: Long): Flow<List<HearingEntity>>

    @Query("SELECT * FROM hearings WHERE id = :id")
    suspend fun getById(id: Long): HearingEntity?

    @Insert
    suspend fun insert(hearing: HearingEntity): Long

    @Update
    suspend fun update(hearing: HearingEntity)

    @Delete
    suspend fun delete(hearing: HearingEntity)
}
