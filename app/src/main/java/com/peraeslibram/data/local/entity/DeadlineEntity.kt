package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.DataSource
import com.peraeslibram.domain.model.Deadline
import com.peraeslibram.domain.model.DeadlineStatus
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.model.TipRadnje
import java.time.Instant
import java.time.LocalDate

/**
 * [brojDana] i [nazivRadnjePrikaz] su snapshot vrednosti kopirane iz [DeadlineRuleEntity]
 * u trenutku kreiranja — buduća izmena pravnog pravila ne sme retroaktivno promeniti
 * već obračunate rokove. Vidi domain model [Deadline] za potpuno obrazloženje.
 */
@Entity(
    tableName = "deadlines",
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("caseId")]
)
data class DeadlineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: Long,
    val tipPostupka: TipPostupka,
    val tipRadnje: TipRadnje,
    val nazivRadnjePrikaz: String,
    val opisCustomRadnje: String?,
    val datumOkidaca: LocalDate,
    val brojDana: Int,
    val izracunatiKrajnjiDatum: LocalDate,
    val originalniKrajnjiDatum: LocalDate?,
    val krajnjiDatumPomeren: Boolean,
    val status: DeadlineStatus,
    val izvor: DataSource,
    val napomena: String?,
    val datumKreiranja: Instant,
    val datumIzmene: Instant
)

fun DeadlineEntity.toDomain() = Deadline(
    id = id,
    caseId = caseId,
    tipPostupka = tipPostupka,
    tipRadnje = tipRadnje,
    nazivRadnjePrikaz = nazivRadnjePrikaz,
    opisCustomRadnje = opisCustomRadnje,
    datumOkidaca = datumOkidaca,
    brojDana = brojDana,
    izracunatiKrajnjiDatum = izracunatiKrajnjiDatum,
    originalniKrajnjiDatum = originalniKrajnjiDatum,
    krajnjiDatumPomeren = krajnjiDatumPomeren,
    status = status,
    izvor = izvor,
    napomena = napomena,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)

fun Deadline.toEntity() = DeadlineEntity(
    id = id,
    caseId = caseId,
    tipPostupka = tipPostupka,
    tipRadnje = tipRadnje,
    nazivRadnjePrikaz = nazivRadnjePrikaz,
    opisCustomRadnje = opisCustomRadnje,
    datumOkidaca = datumOkidaca,
    brojDana = brojDana,
    izracunatiKrajnjiDatum = izracunatiKrajnjiDatum,
    originalniKrajnjiDatum = originalniKrajnjiDatum,
    krajnjiDatumPomeren = krajnjiDatumPomeren,
    status = status,
    izvor = izvor,
    napomena = napomena,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)
