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
import com.cardmanager.app.ui.DashboardScreen
import com.cardmanager.app.ui.theme.CardManagerTheme
import com.cardmanager.app.viewmodel.CardRepository
import com.cardmanager.app.viewmodel.CardViewModel
import com.cardmanager.app.viewmodel.CardViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: CardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Database & Repository
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
                            onCardClick = { cardId -> navController.navigate("card_detail/$cardId") }
                        )
                    }
                    composable("add_card") {
                        // We will build this screen in the next step!
                    }
                    composable("card_detail/{cardId}") { backStackEntry ->
                        // We will build this screen next!
                    }
                }
            }
        }
    }
}

