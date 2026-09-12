package com.peraeslibram.data.local.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

/**
 * Čuva slike priloga predmeta u privatnom skladištu aplikacije (filesDir/prilozi/<caseId>/).
 * Room CASCADE briše samo redove u bazi, nikad fajlove na disku — zato pozivalac (repository)
 * uvek mora eksplicitno da pozove [delete]/[deleteCaseDirectory] uz brisanje odgovarajućih redova.
 */
class AttachmentFileStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private fun caseDir(caseId: Long): File =
        File(context.filesDir, "prilozi/$caseId").apply { if (!exists()) mkdirs() }

    /** Kopira sadržaj [sourceUri] (stranicu iz rezultata skenera) u novi JPEG unutar predmeta. */
    fun importPage(caseId: Long, sourceUri: Uri): File {
        val destination = File(caseDir(caseId), "${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Ne mogu da otvorim izvorni URI: $sourceUri")
        return destination
    }

    fun resolve(caseId: Long, fileName: String): File = File(caseDir(caseId), fileName)

    fun delete(caseId: Long, fileName: String): Boolean = resolve(caseId, fileName).delete()

    fun deleteCaseDirectory(caseId: Long) {
        caseDir(caseId).deleteRecursively()
    }
}
