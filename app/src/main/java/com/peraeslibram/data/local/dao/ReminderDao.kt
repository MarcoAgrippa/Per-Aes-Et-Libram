package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.peraeslibram.data.local.entity.ReminderEntity

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE hearingId = :hearingId")
    suspend fun getForHearing(hearingId: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE deadlineId = :deadlineId")
    suspend fun getForDeadline(deadlineId: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE aktivan = 1 AND poslat = 0")
    suspend fun getPending(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): ReminderEntity?

    @Query("UPDATE reminders SET poslat = 1 WHERE id = :id")
    suspend fun markSent(id: Long)

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE hearingId = :hearingId")
    suspend fun deleteForHearing(hearingId: Long)

    @Query("DELETE FROM reminders WHERE deadlineId = :deadlineId")
    suspend fun deleteForDeadline(deadlineId: Long)
}
