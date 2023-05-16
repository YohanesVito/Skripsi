package com.example.mokuramqtt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Mokura::class, Hardware::class, User::class],
    version = 3,
    exportSchema = false
)
abstract class MokuraDatabase : RoomDatabase() {
    abstract fun mokuraDao(): MokuraDao
    abstract fun hardwareDao(): HardwareDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MokuraDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MokuraDatabase {
            if (INSTANCE == null) {
                synchronized(MokuraDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        MokuraDatabase::class.java, "mokura_database")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE as MokuraDatabase
        }
    }
}
