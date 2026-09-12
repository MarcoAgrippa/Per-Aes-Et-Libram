package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peraeslibram.data.local.entity.NonWorkingDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NonWorkingDayDao {

    @Query("SELECT * FROM non_working_days ORDER BY datum ASC")
    fun observeAll(): Flow<List<NonWorkingDayEntity>>

    @Query("SELECT * FROM non_working_days WHERE godina = :year")
    suspend fun getForYear(year: Int): List<NonWorkingDayEntity>

    @Query("SELECT DISTINCT godina FROM non_working_days")
    suspend fun getSeededYears(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(days: List<NonWorkingDayEntity>)

    @Insert
    suspend fun insert(day: NonWorkingDayEntity): Long

    @Delete
    suspend fun delete(day: NonWorkingDayEntity)
}
