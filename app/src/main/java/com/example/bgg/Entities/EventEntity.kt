package com.example.bgg.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

class EventEntity {
    @Entity(tableName = "events")
    data class EventEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val gameId: Int,
        val gameName: String,
        val tableNumber: Int,
        val numberOfPeople: Int,
        val createdBy: Int
    )

}