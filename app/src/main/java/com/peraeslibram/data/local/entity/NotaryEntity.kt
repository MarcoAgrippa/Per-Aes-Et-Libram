package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.Notary
import java.time.Instant

@Entity(tableName = "notaries")
data class NotaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val naziv: String,
    val adresa: String?,
    val telefon: String?,
    val email: String?,
    val napomena: String?,
    val datumKreiranja: Instant,
    val datumIzmene: Instant
)

fun NotaryEntity.toDomain() = Notary(
    id = id,
    naziv = naziv,
    adresa = adresa,
    telefon = telefon,
    email = email,
    napomena = napomena,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)

fun Notary.toEntity() = NotaryEntity(
    id = id,
    naziv = naziv,
    adresa = adresa,
    telefon = telefon,
    email = email,
    napomena = napomena,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)
