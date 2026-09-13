package com.peraeslibram.domain.ocr

import android.net.Uri

/** Apstrakcija nad OCR mehanizmom (u produkciji: ML Kit) — odvojena radi testabilnosti. */
interface TextRecognizer {
    /** Prepoznaje tekst na slici [imageUri]. Vraća prazan string ako ništa nije prepoznato. */
    suspend fun recognize(imageUri: Uri): String
}
