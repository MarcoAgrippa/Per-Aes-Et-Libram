package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.Reminder
import java.time.Instant

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = HearingEntity::class,
            parentColumns = ["id"],
            childColumns = ["hearingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DeadlineEntity::class,
            parentColumns = ["id"],
            childColumns = ["deadlineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hearingId"), Index("deadlineId")]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hearingId: Long?,
    val deadlineId: Long?,
    val minutesBefore: Long,
    val vremeOkidanja: Instant,
    val notifikacijaId: Int,
    val aktivan: Boolean,
    val poslat: Boolean
)

fun ReminderEntity.toDomain() = Reminder(
    id = id,
    hearingId = hearingId,
    deadlineId = deadlineId,
    minutesBefore = minutesBefore,
    vremeOkidanja = vremeOkidanja,
    notifikacijaId = notifikacijaId,
    aktivan = aktivan,
    poslat = poslat
)

fun Reminder.toEntity() = ReminderEntity(
    id = id,
    hearingId = hearingId,
    deadlineId = deadlineId,
    minutesBefore = minutesBefore,
    vremeOkidanja = vremeOkidanja,
    notifikacijaId = notifikacijaId,
    aktivan = aktivan,
    poslat = poslat
)
