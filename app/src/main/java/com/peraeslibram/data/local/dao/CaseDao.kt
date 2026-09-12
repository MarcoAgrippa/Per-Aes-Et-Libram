package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.CaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    @Query("SELECT * FROM cases ORDER BY datumIzmene DESC")
    fun observeAll(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id")
    fun observeById(id: Long): Flow<CaseEntity?>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getById(id: Long): CaseEntity?

    @Insert
    suspend fun insert(caseEntity: CaseEntity): Long

    @Update
    suspend fun update(caseEntity: CaseEntity)

    @Delete
    suspend fun delete(caseEntity: CaseEntity)
}
