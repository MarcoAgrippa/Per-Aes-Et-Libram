package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.Court
import java.time.Instant

@Entity(tableName = "courts")
data class CourtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val naziv: String,
    val adresa: String?,
    val telefon: String?,
    val email: String?,
    val napomena: String?,
    val datumKreiranja: Instant,
    val datumIzmene: Instant
)

fun CourtEntity.toDomain() = Court(
    id = id,
    naziv = naziv,
    adresa = adresa,
    telefon = telefon,
    email = email,
    napomena = napomena,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)

fun Court.toEntity() = CourtEntity(
    id = id,
    naziv = naziv,
    adresa = adresa,
    telefon = telefon,
    email = email,
    napomena = napomena,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)
