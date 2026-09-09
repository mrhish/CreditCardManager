package com.cardmanager.app.viewmodel

import com.cardmanager.app.data.dao.CardDao
import com.cardmanager.app.data.dao.TransactionDao
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class CardRepository(
    private val cardDao: CardDao,
    private val transactionDao: TransactionDao
) {
    val allCards: Flow<List<Card>> = cardDao.getAllCards()
    
    fun getTransactionsForCard(cardId: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForCard(cardId)
    }

    suspend fun insertCard(card: Card): Long {
        return cardDao.insertCard(card)
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(card)
    }
}
