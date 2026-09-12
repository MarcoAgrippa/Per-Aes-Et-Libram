package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.DeadlineRuleDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.domain.model.DeadlineRule
import com.peraeslibram.domain.repository.DeadlineRuleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeadlineRuleRepositoryImpl @Inject constructor(
    private val dao: DeadlineRuleDao
) : DeadlineRuleRepository {

    override fun observeActiveRules(): Flow<List<DeadlineRule>> =
        dao.observeActive().map { list -> list.map { it.toDomain() } }
}
