package com.example.tradetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tradetracker.data.database.TradeDatabase
import com.example.tradetracker.data.model.Trade
import kotlinx.coroutines.flow.Flow

class TradeListViewModel(application: Application) : AndroidViewModel(application) {
    private val tradeDao = TradeDatabase.getDatabase(application).tradeDao()
    
    val allTrades: Flow<List<Trade>> = tradeDao.getAllTrades()
}