package com.example.bgg.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bgg.Entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<UserEntity>>

    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE name=:username")
    suspend fun getUserByName(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email=:email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id=:id")
    suspend fun getUserById(id: Int): UserEntity?

    @Update
    fun updateUser(user: UserEntity)

    @Delete
    fun deleteUser(user: UserEntity)
}