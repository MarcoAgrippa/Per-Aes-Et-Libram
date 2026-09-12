package com.peraeslibram.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `prilozi` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `caseId` INTEGER NOT NULL,
                `naziv` TEXT NOT NULL,
                `fileName` TEXT NOT NULL,
                `datumKreiranja` INTEGER NOT NULL,
                FOREIGN KEY(`caseId`) REFERENCES `cases`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_prilozi_caseId` ON `prilozi` (`caseId`)"
        )
    }
}
