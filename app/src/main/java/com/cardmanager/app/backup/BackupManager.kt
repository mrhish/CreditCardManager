package com.cardmanager.app.backup

import android.content.Context
import android.net.Uri
import com.cardmanager.app.data.model.Card
import com.cardmanager.app.data.model.Transaction
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object BackupManager {
    private val gson = Gson()

    // EXPORT: Writes database to a file chosen by the user
    suspend fun exportData(context: Context, uri: Uri, cards: List<Card>, transactions: List<Transaction>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val backupData = BackupData(cards, transactions)
                val jsonString = gson.toJson(backupData)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(jsonString)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // IMPORT: Reads the file and gives the data back to the app
    suspend fun importData(context: Context, uri: Uri): BackupData? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val jsonString = reader.readText()
                        gson.fromJson(jsonString, BackupData::class.java)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

