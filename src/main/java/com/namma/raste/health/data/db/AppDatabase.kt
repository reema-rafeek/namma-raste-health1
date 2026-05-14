package com.namma.raste.health.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RoadEntity::class,
        ContractorEntity::class,
        DamageReportEntity::class,
        MaintenanceLogEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roadDao(): RoadDao
    abstract fun contractorDao(): ContractorDao
    abstract fun damageReportDao(): DamageReportDao
    abstract fun maintenanceLogDao(): MaintenanceLogDao
}
