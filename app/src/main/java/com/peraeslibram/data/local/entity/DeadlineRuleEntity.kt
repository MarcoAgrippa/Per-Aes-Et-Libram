package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.DeadlineRule
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.model.TipRadnje

@Entity(
    tableName = "deadline_rules",
    indices = [Index(value = ["tipRadnje"], unique = true)]
)
data class DeadlineRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipPostupka: TipPostupka?,
    val tipRadnje: TipRadnje,
    val brojDana: Int?,
    val nazivPrikaz: String,
    val napomenaPravno: String?,
    val aktivno: Boolean
)

fun DeadlineRuleEntity.toDomain() = DeadlineRule(
    tipPostupka = tipPostupka,
    tipRadnje = tipRadnje,
    brojDana = brojDana,
    nazivPrikaz = nazivPrikaz,
    napomenaPravno = napomenaPravno,
    aktivno = aktivno
)

fun DeadlineRule.toEntity() = DeadlineRuleEntity(
    tipPostupka = tipPostupka,
    tipRadnje = tipRadnje,
    brojDana = brojDana,
    nazivPrikaz = nazivPrikaz,
    napomenaPravno = napomenaPravno,
    aktivno = aktivno
)
