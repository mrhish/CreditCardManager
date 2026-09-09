package com.cardmanager.app.reminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bankName = intent.getStringExtra("BANK_NAME") ?: "Your Credit Card"
        val cardId = intent.getLongExtra("CARD_ID", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "PAYMENT_REMINDER_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your own icon later
            .setContentTitle("Payment Due Tomorrow!")
            .setContentText("Your $bankName bill is due soon. Please clear your balance.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(cardId.toInt(), notification)
    }
}
