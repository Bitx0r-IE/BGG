package com.example.bgg.Entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)