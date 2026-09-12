package com.peraeslibram.data.local.seed

import com.peraeslibram.data.local.AppDatabase
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.di.ApplicationScope
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Popunjava bazu ugrađenim pravnim pravilima ([DeadlineRuleSeeder]) i praznicima
 * ([HolidaySeedData]) za tekuću i narednu godinu, pri prvom kreiranju baze.
 *
 * Prima `Provider<AppDatabase>` (umesto direktno [AppDatabase]) da bi se razbio
 * kružni zavisnosni ciklus: [AppDatabase] se gradi sa callback-om koji zahteva
 * ovaj seeder, a seederu treba baza da upiše podatke — `Provider` odlaže
 * razrešavanje do trenutka kada je graf već izgrađen.
 */
class DatabaseSeeder @Inject constructor(
    private val databaseProvider: Provider<AppDatabase>,
    @ApplicationScope private val scope: CoroutineScope
) {

    fun seedOnCreate() {
        scope.launch(Dispatchers.IO) {
            val db = databaseProvider.get()

            db.deadlineRuleDao().insertAll(
                DeadlineRuleSeeder.defaultRules.map { it.toEntity() }
            )

            val currentYear = LocalDate.now().year
            val holidayEntities = listOf(currentYear, currentYear + 1)
                .flatMap { year -> HolidaySeedData.forYear(year) }
                .map { it.toEntity() }
            db.nonWorkingDayDao().insertAll(holidayEntities)
        }
    }
}
