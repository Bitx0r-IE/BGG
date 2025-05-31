package com.example.bgg.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.EventEntity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    val sharedPref = application.getSharedPreferences("bgg_prefs", MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    private val eventDao = AppDatabase.getDatabase(application).eventDao()

    val events: LiveData<List<EventEntity>> = eventDao.getEventByUserId(userId)
}