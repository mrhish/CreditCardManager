package com.cardmanager.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.data.model.Transaction
import com.cardmanager.app.ui.theme.*
import com.cardmanager.app.viewmodel.CardMetrics
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: Card?,
    transactions: List<Transaction>,
    onNavigateBack: () -> Unit,
    onAddTransaction: (Transaction) -> Unit
) {
    if (card == null) return

    var showTransactionDialog by remember { mutableStateOf(false) }

    val usedBalance = CardMetrics.calculateUsedBalance(transactions)
    val availableBalance = CardMetrics.calculateAvailableBalance(card.totalCreditLimit, transactions)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card.bankName, color = PayPalNavy) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTransactionDialog = true },
                containerColor = PayPalBlue,
                contentColor = SurfaceWhite
            ) { Icon(Icons.Default.Add, "Add Spend") }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            
            // Metrics Header
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Available Balance", color = TextSecondary)
                    Text("LKR $availableBalance", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Spends: LKR $usedBalance", color = CrimsonRed)
                        Text("Limit: LKR ${card.totalCreditLimit}", color = TextSecondary)
                    }
                }
            }

            // Transaction History
            Text("Recent Activity", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, color = PayPalNavy)

            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                items(transactions) { txn ->
                    TransactionItem(txn)
                }
            }
        }
    }

    if (showTransactionDialog) {
        AddTransactionDialog(
            cardId = card.id,
            onDismiss = { showTransactionDialog = false },
            onSave = { 
                onAddTransaction(it)
                showTransactionDialog = false 
            }
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val formatter = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(transaction.timestamp))
    val isExpense = transaction.type == "EXPENSE"

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.category, fontWeight = FontWeight.Bold)
            if (transaction.description.isNotBlank()) {
                Text(transaction.description, fontSize = 12.sp, color = TextSecondary)
            }
            Text(dateString, fontSize = 10.sp, color = TextSecondary)
        }
        Text(
            text = "${if (isExpense) "-" else "+"} LKR ${transaction.amount}",
            fontWeight = FontWeight.Bold,
            color = if (isExpense) CrimsonRed else EmeraldGreen
        )
    }
    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = TextSecondary.copy(alpha = 0.2f))
}

@Composable
fun AddTransactionDialog(cardId: Long, onDismiss: () -> Unit, onSave: (Transaction) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) } // true for Spend, false for Payment

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isExpense) "Add Spend" else "Record Payment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Type: ")
                    Switch(
                        checked = isExpense,
                        onCheckedChange = { isExpense = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CrimsonRed, 
                            uncheckedThumbColor = EmeraldGreen
                        )
                    )
                    Text(if (isExpense) " Expense" else " Settlement")
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (LKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details (e.g., Grocery, Bill)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null && parsedAmount > 0) {
                    onSave(
                        Transaction(
                            cardId = cardId,
                            type = if (isExpense) "EXPENSE" else "SETTLEMENT",
                            amount = parsedAmount,
                            category = if (isExpense) "Spend" else "Payment",
                            description = description
                        )
                    )
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
