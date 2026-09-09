package com.cardmanager.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cardmanager.app.backup.BackupManager
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.data.model.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(private val repository: CardRepository) : ViewModel() {

    val cards: StateFlow<List<Card>> = repository.allCards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getTransactions(cardId: Long): StateFlow<List<Transaction>> {
        return repository.getTransactionsForCard(cardId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addCard(card: Card, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertCard(card)
            onSuccess(id)
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun exportDatabase(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.allCards.collect { currentCards ->
                val allTransactions = mutableListOf<Transaction>()
                for (card in currentCards) {
                    repository.getTransactionsForCard(card.id).collect { txns ->
                        allTransactions.addAll(txns)
                    }
                }
                val success = BackupManager.exportData(context, uri, currentCards, allTransactions)
                onResult(success)
            }
        }
    }

    fun importDatabase(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val backup = BackupManager.importData(context, uri)
            if (backup != null) {
                backup.cards.forEach { repository.insertCard(it) }
                backup.transactions.forEach { repository.addTransaction(it) }
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}

class CardViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
