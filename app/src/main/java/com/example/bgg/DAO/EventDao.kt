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

    @Query("SELECT * FROM events WHERE id=:id")
    suspend fun getEventById(id: Int): EventEntity?

    @Query("Select * FROM events WHERE createdBy=:userId")
    fun getEventByUserId(userId: Int): LiveData<List<EventEntity>>
}