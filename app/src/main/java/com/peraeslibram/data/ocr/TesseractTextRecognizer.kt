package com.peraeslibram.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.googlecode.tesseract.android.TessBaseAPI
import com.peraeslibram.di.IoDispatcher
import com.peraeslibram.domain.ocr.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

private const val TESS_LANGUAGE = "srp+srp_latn"
private val TRAINED_DATA_FILES = listOf("srp.traineddata", "srp_latn.traineddata")

/**
 * Tesseract OCR sa srpskim ćiriličnim i latiničnim modelom (fast varijanta trained data
 * fajlova, upakovana u assets/tessdata). Zamenjuje ML Kit, koji ćirilicu uopšte ne prepoznaje.
 * Kombinovani jezik "srp+srp_latn" pokriva i pozive gde se latinica pojavljuje usred
 * ćiriličnog teksta (rimski brojevi, pečati, potpisi).
 */
class TesseractTextRecognizer @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TextRecognizer {

    /**
     * Tesseract čita *.traineddata isključivo sa diska (ne direktno iz APK assets-a), pa se
     * prvi put kad zatreba raspakuju u files/tesseract/tessdata. Lenjo, da app koji nikad ne
     * skenira poziv ne plaća ni kopiranje ni docnije inicijalizaciju native biblioteke.
     */
    private val dataPath: String by lazy {
        val dir = File(context.filesDir, "tesseract")
        extractTrainedData(dir)
        dir.absolutePath
    }

    private val api: TessBaseAPI by lazy {
        TessBaseAPI().apply { init(dataPath, TESS_LANGUAGE) }
    }

    override suspend fun recognize(imageUri: Uri): String = withContext(ioDispatcher) {
        runCatching {
            val bitmap = decodeBitmap(imageUri) ?: return@runCatching ""
            try {
                synchronized(api) {
                    api.setImage(bitmap)
                    api.utF8Text.orEmpty()
                }
            } finally {
                bitmap.recycle()
            }
        }.getOrDefault("")
    }

    private fun decodeBitmap(imageUri: Uri): Bitmap? =
        context.contentResolver.openInputStream(imageUri)?.use { BitmapFactory.decodeStream(it) }

    private fun extractTrainedData(baseDir: File) {
        val tessdataDir = File(baseDir, "tessdata").apply { mkdirs() }
        TRAINED_DATA_FILES.forEach { fileName ->
            val target = File(tessdataDir, fileName)
            if (target.exists() && target.length() > 0) return@forEach
            context.assets.open("tessdata/$fileName").use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
        }
    }
}
