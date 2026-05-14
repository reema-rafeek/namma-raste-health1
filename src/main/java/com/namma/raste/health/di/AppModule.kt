package com.namma.raste.health.di

import android.content.Context
import androidx.room.Room
import com.namma.raste.health.data.db.*
import com.namma.raste.health.data.repository.RoadRepositoryImpl
import com.namma.raste.health.domain.repository.RoadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "namma_raste_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRoadDao(db: AppDatabase): RoadDao = db.roadDao()

    @Provides
    fun provideContractorDao(db: AppDatabase): ContractorDao = db.contractorDao()

    @Provides
    fun provideReportDao(db: AppDatabase): DamageReportDao = db.damageReportDao()

    @Provides
    fun provideMaintenanceLogDao(db: AppDatabase): MaintenanceLogDao = db.maintenanceLogDao()

    @Provides
    @Singleton
    fun provideRoadRepository(
        roadDao: RoadDao,
        contractorDao: ContractorDao,
        reportDao: DamageReportDao,
        logDao: MaintenanceLogDao
    ): RoadRepository {
        return RoadRepositoryImpl(roadDao, contractorDao, reportDao, logDao)
    }
}
