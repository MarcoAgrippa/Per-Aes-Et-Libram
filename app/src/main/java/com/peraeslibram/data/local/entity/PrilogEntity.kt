package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.Prilog
import java.time.Instant

@Entity(
    tableName = "prilozi",
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
data class PrilogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: Long,
    val naziv: String,
    val fileName: String,
    val datumKreiranja: Instant
)

fun PrilogEntity.toDomain() = Prilog(
    id = id,
    caseId = caseId,
    naziv = naziv,
    fileName = fileName,
    datumKreiranja = datumKreiranja
)

fun Prilog.toEntity() = PrilogEntity(
    id = id,
    caseId = caseId,
    naziv = naziv,
    fileName = fileName,
    datumKreiranja = datumKreiranja
)
