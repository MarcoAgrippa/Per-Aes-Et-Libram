package com.peraeslibram.data.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.peraeslibram.domain.repository.NonWorkingDayRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

/**
 * Osigurava da su neradni dani (praznici) uvek seedovani za tekuću i narednu godinu,
 * jer se pokretni verski praznici (Vaskrs i sl.) menjaju svake godine i ne mogu se
 * trajno hardkodirati. Zakazan periodično (v. Application) kao sigurnosna mreža
 * pored jednokratnog seed-a pri kreiranju baze.
 */
@HiltWorker
class HolidayAutoSeedWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val nonWorkingDayRepository: NonWorkingDayRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val currentYear = LocalDate.now().year
        nonWorkingDayRepository.ensureSeededForYear(currentYear)
        nonWorkingDayRepository.ensureSeededForYear(currentYear + 1)
        return Result.success()
    }
}
