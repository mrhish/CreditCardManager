package com.cardmanager.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Note: In a full app, you will query the Room DB here and loop through
            // your saved cards to call ReminderScheduler.scheduleReminder() for each.
        }
    }
}
