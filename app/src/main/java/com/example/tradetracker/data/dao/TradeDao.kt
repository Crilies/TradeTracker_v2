package com.example.tradetracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tradetracker.data.model.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<Trade>>
    
    @Query("SELECT * FROM trades WHERE id = :tradeId")
    suspend fun getTradeById(tradeId: Long): Trade?
    
    @Insert
    suspend fun insertTrade(trade: Trade)
    
    @Update
    suspend fun updateTrade(trade: Trade)
    
    @Delete
    suspend fun deleteTrade(trade: Trade)
    
    @Query("DELETE FROM trades WHERE id = :tradeId")
    suspend fun deleteTradeById(tradeId: Long)
}