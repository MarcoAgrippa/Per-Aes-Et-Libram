package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.NotaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaryDao {

    @Query("SELECT * FROM notaries ORDER BY naziv COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<NotaryEntity>>

    @Query("SELECT * FROM notaries WHERE id = :id")
    suspend fun getById(id: Long): NotaryEntity?

    @Query("SELECT COUNT(*) FROM notaries")
    suspend fun count(): Int

    @Insert
    suspend fun insert(notary: NotaryEntity): Long

    @Insert
    suspend fun insertAll(notaries: List<NotaryEntity>)

    @Update
    suspend fun update(notary: NotaryEntity)

    @Delete
    suspend fun delete(notary: NotaryEntity)
}
