package com.example.bgg.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bgg.DAO.EventDao
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.EventEntity
import com.example.bgg.R
import com.example.bgg.api.Game
import com.example.bgg.api.searchBoardGames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateEventActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var eventDao: EventDao
    private var selectedGame: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        db = AppDatabase.getDatabase(this)
        eventDao = db.eventDao()

        val searchBox = findViewById<EditText>(R.id.game_search)
        val resultsList = findViewById<ListView>(R.id.game_results)
        val tableInput = findViewById<EditText>(R.id.table_number)
        val peopleInput = findViewById<EditText>(R.id.number_of_people)
        val createButton = findViewById<Button>(R.id.create_event_button)

        val adapter = ArrayAdapter<Game>(this, android.R.layout.simple_list_item_1)
        resultsList.adapter = adapter

        searchBox.addTextChangedListener {
            val query = it.toString()
            if (query.length > 2) {
                CoroutineScope(Dispatchers.IO).launch {
                    val results = searchBoardGames(query)
                    withContext(Dispatchers.Main) {
                        adapter.clear()
                        adapter.addAll(results)
                    }
                }
            }
        }

        resultsList.setOnItemClickListener { _, _, position, _ ->
            selectedGame = adapter.getItem(position)
        }

        createButton.setOnClickListener {
            val tableNumber = tableInput.text.toString().toIntOrNull() ?: 0
            val people = peopleInput.text.toString().toIntOrNull() ?: 0

            selectedGame?.let { game ->
                val newEvent = EventEntity.EventEntity(
                    gameId = game.id,
                    gameName = game.name,
                    tableNumber = tableNumber,
                    numberOfPeople = people,
                    createdBy = 1 // static for now
                )
                CoroutineScope(Dispatchers.IO).launch {
                    eventDao.insert(newEvent)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateEventActivity, "Event Created", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } ?: Toast.makeText(this, "Select a game", Toast.LENGTH_SHORT).show()
        }
    }
}
