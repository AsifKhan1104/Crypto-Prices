package com.crypto.prices.data.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Watchlist::class], version = 1)
abstract class WatchlistDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        private var instance: WatchlistDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): WatchlistDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, WatchlistDatabase::class.java,
                    "trail_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .allowMainThreadQueries()
                    .build()

            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}