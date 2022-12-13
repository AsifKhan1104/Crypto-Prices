package com.crypto.prices.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class Watchlist(
    @PrimaryKey(autoGenerate = false) var id: String,
    var image: String,
    var market_cap: String,
    var market_cap_rank: String,
    var name: String,
    var price: String,
    var priceChange24h: String,
    var symbol: String,
    var currency: String,
    var type: String
)
