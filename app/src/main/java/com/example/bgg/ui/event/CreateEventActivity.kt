package com.example.bgg.ui.event
import java.util.Calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bgg.DAO.EventDao
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.EventEntity
import com.example.bgg.R
import com.example.bgg.api.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Locale


class CreateEventActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var eventDao: EventDao
    private var selectedGame: Game? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        db = AppDatabase.getDatabase(this)
        eventDao = db.eventDao()

        val game_name = findViewById<EditText>(R.id.game_search)
        val tableInput = findViewById<EditText>(R.id.table_number)
        val peopleInput = findViewById<EditText>(R.id.number_of_people)
        val createButton = findViewById<Button>(R.id.create_event_button)
        val eventDateEditText = findViewById<EditText>(R.id.event_date)

        val adapter = ArrayAdapter<Game>(this, android.R.layout.simple_list_item_1)
        /*
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
        */
        eventDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Note: Month is zero-based (0 = January)
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                eventDateEditText.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val sharedPref = getSharedPreferences("bgg_prefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        createButton.setOnClickListener {
            val tableNumber = tableInput.text.toString().toIntOrNull() ?: 0
            val people = peopleInput.text.toString().toIntOrNull() ?: 0

            val dateStr = eventDateEditText.text.toString()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = try {
                formatter.parse(dateStr)
            } catch (e: Exception) {
                null
            }

            if (selectedDate == null) {
                Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEvent = EventEntity(
                gameName = game_name.text.toString(),
                tableNumber = tableNumber,
                numberOfPeople = people,
                createdBy = userId,
                Date = selectedDate
            )
            CoroutineScope(Dispatchers.IO).launch {
                eventDao.insert(newEvent)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateEventActivity, "Event Created", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            /*
            selectedGame?.let { game ->
                val newEvent = EventEntity(
                    gameName = game.name,
                    tableNumber = tableNumber,
                    numberOfPeople = people,
                    createdBy = userId,
                    Date = selectedDate
                )
                CoroutineScope(Dispatchers.IO).launch {
                    eventDao.insert(newEvent)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateEventActivity, "Event Created", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } ?: Toast.makeText(this, "Select a game", Toast.LENGTH_SHORT).show()*/
        }
    }
}
