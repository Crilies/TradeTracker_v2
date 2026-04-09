package com.example.tradetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val type: String, // "BUY" or "SELL"
    val price: Double,
    val quantity: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)