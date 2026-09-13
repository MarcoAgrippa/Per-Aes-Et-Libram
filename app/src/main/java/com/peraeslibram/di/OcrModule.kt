package com.peraeslibram.di

import com.peraeslibram.data.ocr.TesseractTextRecognizer
import com.peraeslibram.domain.ocr.TextRecognizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OcrModule {

    @Binds
    @Singleton
    abstract fun bindTextRecognizer(impl: TesseractTextRecognizer): TextRecognizer
}
