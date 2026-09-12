package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.DeadlineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeadlineDao {

    @Query("SELECT * FROM deadlines ORDER BY izracunatiKrajnjiDatum ASC")
    fun observeAll(): Flow<List<DeadlineEntity>>

    @Query("SELECT * FROM deadlines WHERE caseId = :caseId ORDER BY izracunatiKrajnjiDatum ASC")
    fun observeByCase(caseId: Long): Flow<List<DeadlineEntity>>

    @Query("SELECT * FROM deadlines WHERE id = :id")
    suspend fun getById(id: Long): DeadlineEntity?

    @Insert
    suspend fun insert(deadline: DeadlineEntity): Long

    @Update
    suspend fun update(deadline: DeadlineEntity)

    @Delete
    suspend fun delete(deadline: DeadlineEntity)
}
