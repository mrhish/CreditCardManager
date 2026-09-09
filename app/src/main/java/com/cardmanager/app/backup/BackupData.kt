package com.cardmanager.app.backup

import androidx.annotation.Keep
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.data.model.Transaction

@Keep
data class BackupData(
    val cards: List<Card>,
    val transactions: List<Transaction>
)
