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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `courts` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `naziv` TEXT NOT NULL,
                `adresa` TEXT,
                `telefon` TEXT,
                `email` TEXT,
                `napomena` TEXT,
                `datumKreiranja` INTEGER NOT NULL,
                `datumIzmene` INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}
