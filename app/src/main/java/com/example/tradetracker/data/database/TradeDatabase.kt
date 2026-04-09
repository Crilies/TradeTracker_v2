package com.example.tradetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tradetracker.data.dao.TradeDao
import com.example.tradetracker.data.model.Trade

@Database(entities = [Trade::class], version = 1, exportSchema = false)
abstract class TradeDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    
    companion object {
        @Volatile
        private var INSTANCE: TradeDatabase? = null
        
        fun getDatabase(context: Context): TradeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradeDatabase::class.java,
                    "trade_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}