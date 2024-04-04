package dev.haqim.dailytasktracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dev.haqim.dailytasktracker.data.repository.TaskCategoryRepository
import dev.haqim.dailytasktracker.data.repository.TaskRepository
import dev.haqim.dailytasktracker.domain.repository.ITaskCategoryRepository
import dev.haqim.dailytasktracker.domain.repository.ITaskRepository

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    @ViewModelScoped
    fun provideTaskCategoryRepository(repository: TaskCategoryRepository): ITaskCategoryRepository

    @Binds
    @ViewModelScoped
    fun provideTaskRepository(repository: TaskRepository): ITaskRepository

}

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModuleSingleton {
    
//    @Binds
//    @Singleton
//    fun provideIConnectivityRepository(repository: ConnectivityRepository): IConnectivityRepository

}