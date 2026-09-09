package com.cardmanager.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.data.model.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(private val repository: CardRepository) : ViewModel() {

    // Exposes the list of cards to the UI automatically updated
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
}

// Factory to create the ViewModel without heavy libraries like Hilt
class CardViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
