package com.peraeslibram.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peraeslibram.data.local.entity.DeadlineRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeadlineRuleDao {

    @Query("SELECT * FROM deadline_rules WHERE aktivno = 1")
    fun observeActive(): Flow<List<DeadlineRuleEntity>>

    @Query("SELECT COUNT(*) FROM deadline_rules")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rules: List<DeadlineRuleEntity>)
}
