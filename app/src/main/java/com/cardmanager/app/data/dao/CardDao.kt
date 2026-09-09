package com.cardmanager.app.data.dao

import androidx.room.*
import com.cardmanager.app.data.model.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :cardId LIMIT 1")
    suspend fun getCardById(cardId: Long): Card?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)
}
