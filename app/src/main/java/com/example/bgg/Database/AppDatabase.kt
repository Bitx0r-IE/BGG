package com.example.bgg.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bgg.DAO.EventDao
import com.example.bgg.DAO.UserDao
import com.example.bgg.Entities.EventEntity
import com.example.bgg.Entities.UserEntity

@Database(entities = [UserEntity::class, EventEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "boardgame_app.db")
                    .build().also { instance = it }
            }
        }
    }
}