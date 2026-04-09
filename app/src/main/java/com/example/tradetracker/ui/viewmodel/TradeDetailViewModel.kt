package com.example.tradetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradetracker.data.database.TradeDatabase
import com.example.tradetracker.data.model.Trade
import kotlinx.coroutines.launch

class TradeDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val tradeDao = TradeDatabase.getDatabase(application).tradeDao()
    
    suspend fun getTradeById(tradeId: Long): Trade? {
        return tradeDao.getTradeById(tradeId)
    }
    
    fun deleteTrade(trade: Trade) {
        viewModelScope.launch {
            tradeDao.deleteTrade(trade)
        }
    }
}