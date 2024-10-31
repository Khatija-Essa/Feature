package com.example.nobrainer2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.nobrainer2.OwnQuiz.MyQuiz
import com.example.nobrainer2.OwnQuiz.OwnQuizActivity
import com.example.nobrainer2.databinding.ActivityMainBinding
import com.example.nobrainer2.otherGames.OtherGamesActivity

class MainActivity : BaseActivity() {
    private var binding: ActivityMainBinding? = null

    companion object {
        private const val CHANNEL_ID = "nobrainer_channel"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
        private const val NOTIFICATION_ID = 1
        private const val PENDING_INTENT_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Create notification channel when app starts
        createNotificationChannel()

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        binding?.apply {
            // Your existing click listeners remain the same
            createbtn.setOnClickListener {
                val intent = Intent(this@MainActivity, OwnQuizActivity::class.java)
                startActivity(intent)
            }

            myQuiz.setOnClickListener {
                val intent = Intent(this@MainActivity, MyQuiz::class.java)
                startActivity(intent)
            }

            btnOtherGames.setOnClickListener {
                val intent = Intent(this@MainActivity, OtherGamesActivity::class.java)
                startActivity(intent)
            }

            chatbtn.setOnClickListener {
                val intent = Intent(this@MainActivity, ChatAi::class.java)
                startActivity(intent)
            }

            btn.setOnClickListener {
                val intent = Intent(this@MainActivity, ActivitySettings::class.java)
                startActivity(intent)
            }
        }

        // Show notification when app starts
        showNotification()
    }

    private fun showNotification() {
        // Create an explicit intent for the MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create the PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQUEST_CODE,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        // Build the notification with the PendingIntent
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Automatically removes the notification when tapped
            .setContentIntent(pendingIntent) // Sets the pending intent

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    // Your existing methods remain the same
    private fun requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}