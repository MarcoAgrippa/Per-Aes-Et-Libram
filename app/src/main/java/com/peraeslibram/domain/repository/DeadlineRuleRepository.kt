package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.DeadlineRule
import kotlinx.coroutines.flow.Flow

interface DeadlineRuleRepository {
    fun observeActiveRules(): Flow<List<DeadlineRule>>
}
