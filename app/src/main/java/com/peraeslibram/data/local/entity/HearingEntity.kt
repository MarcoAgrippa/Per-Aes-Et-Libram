package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.Hearing
import com.peraeslibram.domain.model.HearingStatus
import java.time.Instant
import java.time.LocalDateTime

@Entity(
    tableName = "hearings",
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
data class HearingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: Long,
    val datumVreme: LocalDateTime,
    val sud: String?,
    val sudnica: String?,
    val tipRocista: String?,
    val napomena: String?,
    val status: HearingStatus,
    val datumKreiranja: Instant,
    val datumIzmene: Instant
)

fun HearingEntity.toDomain() = Hearing(
    id = id,
    caseId = caseId,
    datumVreme = datumVreme,
    sud = sud,
    sudnica = sudnica,
    tipRocista = tipRocista,
    napomena = napomena,
    status = status,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)

fun Hearing.toEntity() = HearingEntity(
    id = id,
    caseId = caseId,
    datumVreme = datumVreme,
    sud = sud,
    sudnica = sudnica,
    tipRocista = tipRocista,
    napomena = napomena,
    status = status,
    datumKreiranja = datumKreiranja,
    datumIzmene = datumIzmene
)
