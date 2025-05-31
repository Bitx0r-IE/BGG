package com.example.bgg.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.EventEntity

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao = AppDatabase.getDatabase(application).eventDao()

    val events: LiveData<List<EventEntity>> = eventDao.getAll()
}