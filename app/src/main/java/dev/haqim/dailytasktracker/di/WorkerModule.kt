package dev.haqim.dailytasktracker.di

import dev.haqim.dailytasktracker.data.remote.base.ApiConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Unscoped

@Module
@InstallIn(SingletonComponent::class)
interface IWorkerModule {
//    @Unscoped
//    @Binds
//    fun provideIVoteRepository(repository: VoteRepository): IVoteRepository
//
//    @Unscoped
//    @Binds
//    fun provideIUploadEvidenceRepository(repository: UploadEvidenceRepository): IUploadEvidenceRepository
//
//    @Binds
//    fun provideIOfflineRepository(repository: OfflineRepository): IOfflineRepository
}

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    
//    @Provides
//    fun provideOfflineService(
//        @AuthorizedApiConfigUnscoped
//        apiConfig: ApiConfig
//    ): OfflineService = apiConfig.createService(OfflineService::class.java)
}