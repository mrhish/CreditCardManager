package com.cardmanager.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val cardId: Long,
    val type: String, // "EXPENSE" or "SETTLEMENT"
    val amount: Double,
    val category: String = "General", // Shopping, Fuel, Dining, Bills, etc.
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
