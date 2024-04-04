package dev.haqim.dailytasktracker.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.haqim.dailytasktracker.data.local.entity.table.RemoteKeys
import dev.haqim.dailytasktracker.data.local.entity.table.TaskAndCategoryCrossRef
import dev.haqim.dailytasktracker.data.local.entity.table.TaskCategoryEntity
import dev.haqim.dailytasktracker.data.local.entity.table.TaskEntity
import dev.haqim.dailytasktracker.data.local.entity.typeConverter.UriConverter
import dev.haqim.dailytasktracker.data.local.room.dao.RemoteKeysDao
import dev.haqim.dailytasktracker.data.local.room.dao.TaskAndCategoryCrossRefDao
import dev.haqim.dailytasktracker.data.local.room.dao.TaskCategoryDao
import dev.haqim.dailytasktracker.data.local.room.dao.TaskDao

@Database(
    entities = [
        RemoteKeys::class,
        TaskCategoryEntity::class,
        TaskEntity::class,
        TaskAndCategoryCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(UriConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun taskCategoryDao(): TaskCategoryDao
    abstract fun taskDao(): TaskDao
    abstract fun taskAndCategoryCrossRefDao(): TaskAndCategoryCrossRefDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_task_tracker.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { 
                        INSTANCE = it
                    }
            }
        }
    }
}