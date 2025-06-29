package com.example.diagnostic333

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DtcsMsg::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dtcsMsgDao(): DtcsMsgDao

    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diagnostic_db"
                ).build().also { instance = it }
            }
        }
    }
}