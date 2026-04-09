package com.example.tradetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradetracker.data.database.TradeDatabase
import com.example.tradetracker.data.model.Trade
import com.example.tradetracker.notification.SuperIslandHelper
import kotlinx.coroutines.launch

class AddTradeViewModel(application: Application) : AndroidViewModel(application) {
    private val tradeDao = TradeDatabase.getDatabase(application).tradeDao()
    
    fun addTrade(
        symbol: String,
        type: String,
        price: Double,
        quantity: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val trade = Trade(
                symbol = symbol,
                type = type,
                price = price,
                quantity = quantity,
                notes = notes
            )
            tradeDao.insertTrade(trade)
            
            // Show Super Island notification
            SuperIslandHelper.showTradeNotification(trade)
        }
    }
}