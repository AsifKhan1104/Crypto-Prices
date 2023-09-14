package com.crypto.prices.data.offline

import android.content.Context

class WatchlistRepo(context: Context) {
    private var watchlistDao: WatchlistDao

    private val database = WatchlistDatabase.getInstance(context)

    init {
        watchlistDao = database.watchlistDao()
    }

    fun insert(watchlist: Watchlist) {
        watchlistDao.insert(watchlist)
    }

    fun delete(id: String) {
        watchlistDao.delete(id)
    }

    fun getAllData(): List<Watchlist> {
        return watchlistDao.getAllData()
    }

    fun getData(id: String): Watchlist {
        return watchlistDao.getData(id)
    }

    fun isWatchlisted(id: String): Boolean {
        if (getData(id) != null)
            return true
        return false
    }
}