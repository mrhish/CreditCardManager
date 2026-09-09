package com.cardmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cardmanager.app.data.AppDatabase
import com.cardmanager.app.ui.AddCardScreen
import com.cardmanager.app.ui.CardDetailScreen
import com.cardmanager.app.ui.DashboardScreen
import com.cardmanager.app.ui.theme.CardManagerTheme
import com.cardmanager.app.viewmodel.CardRepository
import com.cardmanager.app.viewmodel.CardViewModel
import com.cardmanager.app.viewmodel.CardViewModelFactory
import com.cardmanager.app.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: CardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Database, Repository, and ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = CardRepository(database.cardDao(), database.transactionDao())
        val factory = CardViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CardViewModel::class.java]

        setContent {
            CardManagerTheme {
                // 2. Set up the Navigation Controller
                val navController = rememberNavController()
                
                // 3. Observe the list of cards from the ViewModel
                val cards by viewModel.cards.collectAsState()

                // 4. Define all the screens in the app
                NavHost(navController = navController, startDestination = "dashboard") {
                    
                    // SCREEN A: The Main Dashboard
                    composable("dashboard") {
                        DashboardScreen(
                            cards = cards,
                            onAddCardClick = { navController.navigate("add_card") },
                            onCardClick = { cardId -> navController.navigate("card_detail/$cardId") }
                        )
                    }
                    
                    // SCREEN B: Add a New Card
                    composable("add_card") {
                        AddCardScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onSaveCard = { newCard ->
                                // Save the card to the database
                                viewModel.addCard(newCard) { newCardId ->
                                    
                                    // Set up the exact offline alarm for the due date
                                    ReminderScheduler.scheduleReminder(
                                        context = this@MainActivity,
                                        cardId = newCardId,
                                        bankName = newCard.bankName,
                                        dayOfMonth = newCard.paymentDueDay,
                                        hour = 9, // Remind at 9:00 AM
                                        minute = 0
                                    )
                                    
                                    // Go back to the dashboard
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                    
                    // SCREEN C: View Card Details & Add Spends
                    composable("card_detail/{cardId}") { backStackEntry ->
                        // Extract the card ID from the navigation route
                        val cardIdString = backStackEntry.arguments?.getString("cardId")
                        val cardId = cardIdString?.toLongOrNull()
                        
                        // Find the matching card
                        val card = cards.find { it.id == cardId }
                        
                        // Get all transactions (spends/payments) for this specific card
                        val transactions flow = if (cardId != null) {
                            viewModel.getTransactions(cardId)
                        } else {
                            MutableStateFlow(emptyList())
                        }
                        val transactionsList by flow.collectAsState()

                        // Show the detail screen
                        CardDetailScreen(
                            card = card,
                            transactions = transactionsList,
                            onNavigateBack = { navController.popBackStack() },
                            onAddTransaction = { newTransaction -> 
                                viewModel.addTransaction(newTransaction) 
                            }
                        )
                    }
                }
            }
        }
    }
}

