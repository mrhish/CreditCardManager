package com.cardmanager.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.ui.theme.BackgroundLight
import com.cardmanager.app.ui.theme.PayPalBlue
import com.cardmanager.app.ui.theme.PayPalNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    onNavigateBack: () -> Unit,
    onSaveCard: (Card) -> Unit
) {
    var bankName by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var lastFour by remember { mutableStateOf("") }
    var creditLimit by remember { mutableStateOf("") }
    var billingDay by remember { mutableStateOf("") }
    var dueDay by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Card", color = PayPalNavy) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name (e.g., Commercial Bank)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it },
                label = { Text("Name on Card") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = lastFour,
                onValueChange = { if (it.length <= 4) lastFour = it },
                label = { Text("Last 4 Digits") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = creditLimit,
                onValueChange = { creditLimit = it },
                label = { Text("Total Credit Limit (LKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = billingDay,
                    onValueChange = { billingDay = it },
                    label = { Text("Statement Date (1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dueDay,
                    onValueChange = { dueDay = it },
                    label = { Text("Due Date (1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (bankName.isNotBlank() && creditLimit.isNotBlank()) {
                        val newCard = Card(
                            bankName = bankName,
                            cardHolderName = cardHolder,
                            lastFourDigits = lastFour,
                            totalCreditLimit = creditLimit.toDoubleOrNull() ?: 0.0,
                            billingCycleDay = billingDay.toIntOrNull() ?: 1,
                            paymentDueDay = dueDay.toIntOrNull() ?: 1
                        )
                        onSaveCard(newCard)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PayPalBlue)
            ) {
                Text("Save Card")
            }
        }
    }
}

