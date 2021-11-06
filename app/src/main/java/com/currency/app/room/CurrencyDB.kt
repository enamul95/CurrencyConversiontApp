package com.currency.app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CurrecnyRoomModel::class,PauseRefreshRoomModel::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDB : RoomDatabase() {
    abstract fun CurrencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: CurrencyDB? = null

        fun getDatabase(context: Context): CurrencyDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                // var instance = Room.databaseBuilder()
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDB::class.java,
                    "currencydb"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}