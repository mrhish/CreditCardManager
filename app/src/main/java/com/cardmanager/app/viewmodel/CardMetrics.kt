package com.cardmanager.app.viewmodel

import com.cardmanager.app.data.model.Transaction

object CardMetrics {
    fun calculateUsedBalance(transactions: List<Transaction>): Double {
        var used = 0.0
        for (txn in transactions) {
            if (txn.type == "EXPENSE") {
                used += txn.amount
            } else if (txn.type == "SETTLEMENT") {
                used -= txn.amount
            }
        }
        return if (used < 0) 0.0 else used
    }

    fun calculateAvailableBalance(totalLimit: Double, transactions: List<Transaction>): Double {
        val used = calculateUsedBalance(transactions)
        return totalLimit - used
    }
}
