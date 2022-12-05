package com.crypto.prices.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class Watchlist(
    @PrimaryKey(autoGenerate = false) val id: String,
    val image: String,
    val market_cap: String,
    val market_cap_rank: String,
    val name: String,
    val price: String,
    val priceChange24h: String,
    val symbol: String,
    val currency: String,
    val type: String
)
