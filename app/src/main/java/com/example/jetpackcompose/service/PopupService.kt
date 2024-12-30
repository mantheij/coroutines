package com.example.jetpackcompose.service

import android.app.*
import android.content.*
import android.os.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.PendingIntent
import androidx.core.content.ContextCompat
import com.example.jetpackcompose.MainActivity
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jetpackcompose.ui.views.dataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PopupService : Service() {

    private val handler = Handler(Looper.getMainLooper()) // Main thread handler for notifications
    private var delayMillis: Long = -1L // Delay for notifications in milliseconds
    private var i = 0 // Counter for notification messages
    private val dataStore by lazy { applicationContext.dataStore } // Lazy initialization of DataStore
    private var isNotificationEnabled: Boolean = false // Flag for notification status

    // BroadcastReceiver to listen for timer updates
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newTimerOption = intent?.getStringExtra("timer_option") ?: "Deactivated"
            updateTimerOption(newTimerOption)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // Create notification channel

        // Initialize notification logic from settings
        CoroutineScope(Dispatchers.IO).launch {
            val timerOption = fetchTimerOptionFromSettings()
            delayMillis = timerOptionToMillis(timerOption)
            if (delayMillis != -1L) {
                isNotificationEnabled = true
                withContext(Dispatchers.Main) {
                    startForegroundService() // Start Foreground service
                    handler.post(showNotificationRunnable)
                }
            }
        }

        registerUpdateReceiver() // Register receiver for timer updates
        initializeTimerFromSettings() // Initialize timer
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(showNotificationRunnable) // Stop notifications
        unregisterReceiver(updateReceiver) // Unregister receiver
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (delayMillis != -1L) {
            handler.removeCallbacks(showNotificationRunnable) // Restart notifications
            handler.post(showNotificationRunnable)
        }
        return START_STICKY // Keep service alive
    }

    override fun onBind(intent: Intent?): IBinder? = null // Service binding not used

    // Runnable to show notifications periodically
    private val showNotificationRunnable = object : Runnable {
        override fun run() {
            if (isNotificationEnabled) {
                sendNotification("Hello World $i") // Send notification
                i++
            }
            handler.postDelayed(this, delayMillis) // Schedule next notification
        }
    }

    // Update notification timer based on new option
    private fun updateTimerOption(option: String) {
        delayMillis = timerOptionToMillis(option)
        isNotificationEnabled = delayMillis != -1L
        handler.removeCallbacks(showNotificationRunnable)

        if (delayMillis == -1L) {
            stopSelf() // Stop service if timer is deactivated
        } else {
            handler.postDelayed(showNotificationRunnable, delayMillis)
        }
    }

    // Fetch saved timer option from DataStore
    private suspend fun fetchTimerOptionFromSettings(): String {
        val key = stringPreferencesKey("timer_option_key")
        return dataStore.data.map { preferences ->
            preferences[key] ?: "Deactivated"
        }.first()
    }

    // Register receiver for timer updates
    private fun registerUpdateReceiver() {
        ContextCompat.registerReceiver(
            this,
            updateReceiver,
            IntentFilter("com.example.jetpackcompose.UPDATE_TIMER"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    // Convert timer option string to milliseconds
    private fun timerOptionToMillis(option: String): Long {
        return when (option) {
            "10s" -> 10_000L
            "30s" -> 30_000L
            "60s" -> 60_000L
            "30 min" -> 30 * 60 * 1000L
            "60 min" -> 60 * 60 * 1000L
            else -> -1L
        }
    }

    // Initialize notification timer from saved settings
    private fun initializeTimerFromSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val timerOption = fetchTimerOptionFromSettings()
            delayMillis = timerOptionToMillis(timerOption)
            if (delayMillis != -1L) {
                isNotificationEnabled = true
                handler.post(showNotificationRunnable)
            }
        }
    }

    // Send notification with given message
    private fun sendNotification(message: String) {
        if (ActivityCompat.checkSelfPermission(
                this@PopupService,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return // Return if notification permission is not granted
        }

        val notificationManager = NotificationManagerCompat.from(this)
        val notification = getNotification(message)
        notificationManager.notify(1, notification) // Show notification
    }

    // Build notification with given content
    private fun getNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "popup_service_channel")
            .setContentTitle("Popup Service") // Notification title
            .setContentText(contentText) // Notification message
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icon for notification
            .setPriority(NotificationCompat.PRIORITY_MAX) // High priority
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent) // Launch MainActivity on click
            .setAutoCancel(true) // Auto-dismiss notification on click
            .build()
    }

    // Create notification channel for Android O and above
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "popup_service_channel",
                "Popup Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications from Popup Service"
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel) // Register notification channel
        }
    }

    // Start the service as a Foreground service with a notification
    private fun startForegroundService() {
        val notification = getNotification("Popup Service Running")
        startForeground(1, notification) // Start the service in the foreground with the notification
    }
}