package com.crypto.prices.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WatchlistDao {

    @Insert
    fun insert(watchlist: Watchlist)

    @Update
    fun update(watchlist: Watchlist)

    @Query("delete from watchlist WHERE id = :id")
    fun delete(id: String)

    @Query("delete from watchlist")
    fun deleteAllData()

    @Query("select * from watchlist order by price desc")
    fun getAllData(): List<Watchlist>

    @Query("select * from watchlist WHERE id =:id")
    fun getData(id: String): Watchlist
}