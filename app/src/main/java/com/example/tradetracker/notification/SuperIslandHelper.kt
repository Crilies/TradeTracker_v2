package com.example.tradetracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tradetracker.MainActivity
import com.example.tradetracker.R
import com.example.tradetracker.data.model.Trade
import com.google.gson.Gson
import com.google.gson.JsonObject

object SuperIslandHelper {
    private const val CHANNEL_ID = "trade_tracker_channel"
    private const val CHANNEL_NAME = "Trade Updates"
    private const val NOTIFICATION_ID = 1001
    
    private val gson = Gson()
    
    fun isSuperIslandSupported(): Boolean {
        // Check if device is Xiaomi and supports Super Island
        return Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) &&
               Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
    
    fun hasFocusNotificationPermission(): Boolean {
        // This is a simplified check. In a real app, you'd check the actual permission
        return isSuperIslandSupported()
    }
    
    fun showTradeNotification(trade: Trade) {
        val context = TradeTrackerApplication.getAppContext()
        createNotificationChannel(context)
        
        val notification = buildNotification(context, trade)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for trade updates"
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun buildNotification(context: Context, trade: Trade): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build Super Island parameters
        val focusParams = buildFocusParams(trade)
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Trade Added: ${trade.symbol}")
            .setContentText("${trade.type} ${trade.quantity} shares at $${trade.price}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                // Add Super Island parameters
                if (isSuperIslandSupported()) {
                    setCustomContentView(null) // Use default for now
                    addMiuiFocusParam(focusParams)
                }
            }
            .build()
    }
    
    private fun buildFocusParams(trade: Trade): String {
        val focusObject = JsonObject().apply {
            addProperty("ticker", "${trade.symbol} ${trade.type}")
            addProperty("tickerPic", "") // URL to image
            addProperty("aodTitle", "Trade: ${trade.symbol}")
            addProperty("aodPic", "") // URL to image
            
            // Summary for collapsed state
            add("summary", JsonObject().apply {
                addProperty("title", "${trade.symbol} ${trade.type}")
                addProperty("content", "${trade.quantity} shares at $${trade.price}")
            })
            
            // Focus for expanded state
            add("focus", JsonObject().apply {
                addProperty("title", "${trade.symbol} ${trade.type}")
                addProperty("content", "Quantity: ${trade.quantity}\nPrice: $${trade.price}\nTotal: $${trade.price * trade.quantity}")
                if (trade.notes.isNotBlank()) {
                    addProperty("extra", "Notes: ${trade.notes}")
                }
            })
            
            // Interaction capabilities
            add("interaction", JsonObject().apply {
                addProperty("type", "trade_update")
                addProperty("tradeId", trade.id)
            })
            
            // Custom actions
            add("miui.focus.actions", gson.toJsonTree(listOf(
                mapOf("id" to "view", "title" to "View Trade"),
                mapOf("id" to "dismiss", "title" to "Dismiss")
            )))
            
            // Share data for drag and drop
            add("shareData", JsonObject().apply {
                addProperty("text", "Trade: ${trade.symbol} ${trade.type} ${trade.quantity} shares at $${trade.price}")
            })
        }
        
        return gson.toJson(focusObject)
    }
    
    // Extension function to add miui.focus.param
    private fun NotificationCompat.Builder.addMiuiFocusParam(param: String): NotificationCompat.Builder {
        // This is a placeholder. In a real implementation, you'd use MIUI's specific API
        // For now, we'll add it as an extra
        setExtras(android.os.Bundle().apply {
            putString("miui.focus.param", param)
        })
        return this
    }
}

// Application class to provide context
class TradeTrackerApplication : android.app.Application() {
    companion object {
        private lateinit var instance: TradeTrackerApplication
        
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}