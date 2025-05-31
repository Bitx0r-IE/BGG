package com.example.bgg.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bgg.Entities.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAll(): LiveData<List<EventEntity>>

    @Insert
    suspend fun insert(event: EventEntity)
}