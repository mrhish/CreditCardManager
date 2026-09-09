package com.cardmanager.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cardmanager.app.data.AppDatabase
import com.cardmanager.app.reminder.ReminderScheduler
import com.cardmanager.app.ui.AddCardScreen
import com.cardmanager.app.ui.CardDetailScreen
import com.cardmanager.app.ui.DashboardScreen
import com.cardmanager.app.ui.theme.CardManagerTheme
import com.cardmanager.app.viewmodel.CardRepository
import com.cardmanager.app.viewmodel.CardViewModel
import com.cardmanager.app.viewmodel.CardViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: CardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = CardRepository(database.cardDao(), database.transactionDao())
        val factory = CardViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CardViewModel::class.java]

        setContent {
            CardManagerTheme {
                val navController = rememberNavController()
                val cards by viewModel.cards.collectAsState()

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(
                            cards = cards,
                            onAddCardClick = { navController.navigate("add_card") },
                            onCardClick = { cardId -> navController.navigate("card_detail/$cardId") },
                            onExportDatabase = { uri ->
                                viewModel.exportDatabase(this@MainActivity, uri) { success ->
                                    val msg = if (success) "Backup Successful!" else "Backup Failed"
                                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onImportDatabase = { uri ->
                                viewModel.importDatabase(this@MainActivity, uri) { success ->
                                    val msg = if (success) "Restore Successful!" else "Restore Failed"
                                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    composable("add_card") {
                        AddCardScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onSaveCard = { newCard ->
                                viewModel.addCard(newCard) { newCardId ->
                                    ReminderScheduler.scheduleReminder(
                                        context = this@MainActivity,
                                        cardId = newCardId,
                                        bankName = newCard.bankName,
                                        dayOfMonth = newCard.paymentDueDay,
                                        hour = 9,
                                        minute = 0
                                    )
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable("card_detail/{cardId}") { backStackEntry ->
                        val cardIdString = backStackEntry.arguments?.getString("cardId")
                        val cardId = cardIdString?.toLongOrNull()

                        val card = cards.find { it.id == cardId }

                        val transactionsFlow = if (cardId != null) {
                            viewModel.getTransactions(cardId)
                        } else {
                            MutableStateFlow(emptyList())
                        }
                        val transactionsList by transactionsFlow.collectAsState()

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
