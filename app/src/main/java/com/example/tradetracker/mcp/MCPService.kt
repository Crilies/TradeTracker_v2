package com.example.tradetracker.mcp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.tradetracker.data.database.TradeDatabase
import com.example.tradetracker.data.model.Trade
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class MCPService : Service() {
    private lateinit var serverSocket: ServerSocket
    private var isRunning = false
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "MCPService"
        private const val PORT = 8080
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onCreate() {
        super.onCreate()
        startServer()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopServer()
    }
    
    private fun startServer() {
        isRunning = true
        scope.launch {
            try {
                serverSocket = ServerSocket(PORT)
                Log.i(TAG, "MCP Server started on port $PORT")
                while (isRunning) {
                    val clientSocket = serverSocket.accept()
                    handleClient(clientSocket)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Server error: ${e.message}")
            }
        }
    }
    
    private fun stopServer() {
        isRunning = false
        try {
            serverSocket.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing server: ${e.message}")
        }
    }
    
    private fun handleClient(socket: Socket) {
        scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer: OutputStream = socket.getOutputStream()
                
                val request = reader.readLine()
                if (request != null) {
                    val response = processRequest(request)
                    writer.write(response.toByteArray())
                    writer.flush()
                }
                socket.close()
            } catch (e: Exception) {
                Log.e(TAG, "Client handling error: ${e.message}")
            }
        }
    }
    
    private suspend fun processRequest(requestJson: String): String {
        return try {
            val request = JsonParser.parseString(requestJson).asJsonObject
            val method = request.get("method")?.asString
            val params = request.get("params")?.asJsonObject
            val id = request.get("id")
            
            val result = when (method) {
                "getTrades" -> getTrades(params)
                "getTradeById" -> getTradeById(params)
                "addTrade" -> addTrade(params)
                "deleteTrade" -> deleteTrade(params)
                else -> JsonObject().apply {
                    addProperty("error", "Unknown method: $method")
                }
            }
            
            val response = JsonObject().apply {
                add("result", result)
                add("id", id)
            }
            gson.toJson(response)
        } catch (e: Exception) {
            val errorResponse = JsonObject().apply {
                addProperty("error", "Invalid request: ${e.message}")
            }
            gson.toJson(errorResponse)
        }
    }
    
    private suspend fun getTrades(params: JsonObject?): JsonObject {
        val tradeDao = TradeDatabase.getDatabase(this).tradeDao()
        val trades = mutableListOf<Trade>()
        
        // Since getAllTrades returns Flow, we need to collect it
        // For simplicity, we'll use a synchronous approach
        // In a real implementation, you'd use proper async handling
        tradeDao.getAllTrades().collect { tradeList ->
            trades.addAll(tradeList)
        }
        
        return JsonObject().apply {
            add("trades", gson.toJsonTree(trades))
        }
    }
    
    private suspend fun getTradeById(params: JsonObject?): JsonObject {
        val tradeId = params?.get("tradeId")?.asLong ?: 0L
        val tradeDao = TradeDatabase.getDatabase(this).tradeDao()
        val trade = tradeDao.getTradeById(tradeId)
        
        return JsonObject().apply {
            add("trade", gson.toJsonTree(trade))
        }
    }
    
    private suspend fun addTrade(params: JsonObject?): JsonObject {
        val symbol = params?.get("symbol")?.asString ?: ""
        val type = params?.get("type")?.asString ?: "BUY"
        val price = params?.get("price")?.asDouble ?: 0.0
        val quantity = params?.get("quantity")?.asInt ?: 0
        val notes = params?.get("notes")?.asString ?: ""
        
        val trade = Trade(
            symbol = symbol,
            type = type,
            price = price,
            quantity = quantity,
            notes = notes
        )
        
        val tradeDao = TradeDatabase.getDatabase(this).tradeDao()
        tradeDao.insertTrade(trade)
        
        return JsonObject().apply {
            addProperty("success", true)
            addProperty("tradeId", trade.id)
        }
    }
    
    private suspend fun deleteTrade(params: JsonObject?): JsonObject {
        val tradeId = params?.get("tradeId")?.asLong ?: 0L
        val tradeDao = TradeDatabase.getDatabase(this).tradeDao()
        tradeDao.deleteTradeById(tradeId)
        
        return JsonObject().apply {
            addProperty("success", true)
        }
    }
}