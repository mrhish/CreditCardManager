package com.cardmanager.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val bankName: String,
    val cardHolderName: String,
    val lastFourDigits: String,
    val cardNetwork: String = "VISA", // VISA, MASTERCARD, AMEX, etc.
    val totalCreditLimit: Double,
    val billingCycleDay: Int,          // 1 - 31
    val paymentDueDay: Int,            // 1 - 31
    val reminderHour: Int = 9,         // 24-hr format (default 9:00 AM)
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true,
    val cardColorHex: String = "#003087" // Default PayPal Deep Navy
)
