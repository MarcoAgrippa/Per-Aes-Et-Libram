package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.Case
import com.peraeslibram.domain.model.CaseStatus
import com.peraeslibram.domain.model.TipPostupka
import java.time.Instant

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val naziv: String,
    val brojPredmeta: String?,
    val klijentIme: String,
    val klijentKontakt: String?,
    val sud: String?,
    val tipPostupka: TipPostupka,
    val napomena: String?,
    val status: CaseStatus,
    val datumKreiranja: Instant,
    val datumIzmene: Instant
)

fun CaseEntity.toDomain() = Case(
    id = id,
    naziv = naziv,
    brojPredmeta = brojPredmeta,
    klijentIme = klijentIme,
    klijentKontakt = klijentKontakt,
    sud = sud,
    tipPostupka = tipPostupka,
    napomena = napomena,
    status = status,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)

fun Case.toEntity() = CaseEntity(
    id = id,
    naziv = naziv,
    brojPredmeta = brojPredmeta,
    klijentIme = klijentIme,
    klijentKontakt = klijentKontakt,
    sud = sud,
    tipPostupka = tipPostupka,
    napomena = napomena,
    status = status,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)
