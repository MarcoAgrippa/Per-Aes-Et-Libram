package com.peraeslibram.data.local.storage

import android.content.Context
import android.net.Uri
import com.peraeslibram.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Čuva slike priloga predmeta u privatnom skladištu aplikacije (filesDir/prilozi/<caseId>/).
 * Room CASCADE briše samo redove u bazi, nikad fajlove na disku — zato pozivalac (repository)
 * uvek mora eksplicitno da pozove [delete]/[deleteCaseDirectory] uz brisanje odgovarajućih redova.
 *
 * Svaka operacija koja dodiruje disk je `suspend` i prebacuje se na [ioDispatcher]; jedini
 * izuzetak je [resolve], koje je čisto računanje putanje i zato sme iz kompozicije.
 */
class AttachmentFileStore @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private fun caseDir(caseId: Long): File = File(context.filesDir, "prilozi/$caseId")

    /** Kopira sadržaj [sourceUri] (stranicu iz rezultata skenera) u novi JPEG unutar predmeta. */
    suspend fun importPage(caseId: Long, sourceUri: Uri): File = withContext(ioDispatcher) {
        val dir = caseDir(caseId).apply { if (!exists()) mkdirs() }
        val destination = File(dir, "${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Ne mogu da otvorim izvorni URI: $sourceUri")
        destination
    }

    /**
     * Samo sastavlja putanju — ne dira disk i ne pravi direktorijum, pa sme da se zove iz
     * kompozicije. Direktorijum kreira [importPage], jedina operacija koja tamo i upisuje.
     */
    fun resolve(caseId: Long, fileName: String): File = File(caseDir(caseId), fileName)

    suspend fun delete(caseId: Long, fileName: String): Boolean = withContext(ioDispatcher) {
        resolve(caseId, fileName).delete()
    }

    suspend fun deleteCaseDirectory(caseId: Long) {
        withContext(ioDispatcher) { caseDir(caseId).deleteRecursively() }
    }
}
