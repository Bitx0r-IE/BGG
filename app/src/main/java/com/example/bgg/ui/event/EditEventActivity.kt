package com.example.bgg.ui.event

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditEventActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var eventDao: EventDao
    private var selectedEvent: EventEntity? = null
    private var selectedGame: Game? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        db = AppDatabase.getDatabase(this)
        eventDao = db.eventDao()

        val gameNameInput = findViewById<AutoCompleteTextView>(R.id.game_search)  // use AutoCompleteTextView for dropdown
        val tableInput = findViewById<EditText>(R.id.table_number)
        val peopleInput = findViewById<EditText>(R.id.number_of_people)
        val dateInput = findViewById<EditText>(R.id.event_date)
        val updateButton = findViewById<Button>(R.id.create_event_button)
        val deleteButton = findViewById<Button>(R.id.delete_event_button)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Adapter for game autocomplete
        val adapter = ArrayAdapter<Game>(this, android.R.layout.simple_dropdown_item_1line)
        gameNameInput.setAdapter(adapter)

        // Listen for typing and fetch game results (debounce or length check can be added)
        gameNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.length > 2) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val results = searchBoardGames(query)  // Your suspend function to get games
                            withContext(Dispatchers.Main) {
                                adapter.clear()
                                adapter.addAll(results)
                                adapter.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            // Handle network or other errors here if needed
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) { }
        })

        // When user clicks an autocomplete suggestion, update selectedGame
        gameNameInput.setOnItemClickListener { _, _, position, _ ->
            selectedGame = adapter.getItem(position)
        }

        val eventId = intent.getIntExtra("eventId", -1)
        if (eventId == -1) {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            selectedEvent = eventDao.getEventById(eventId)
            selectedEvent?.let { event ->
                withContext(Dispatchers.Main) {
                    gameNameInput.setText(event.gameName)
                    tableInput.setText(event.tableNumber.toString())
                    peopleInput.setText(event.numberOfPeople.toString())
                    dateInput.setText(formatter.format(event.Date))
                }
            }
        }

        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentText = dateInput.text.toString()
            if (currentText.isNotBlank()) {
                try {
                    val date = formatter.parse(currentText)
                    calendar.time = date!!
                } catch (e: Exception) {
                    // ignore parse error fallback
                }
            }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                dateInput.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, year, month, day)

            datePicker.show()
        }

        updateButton.setOnClickListener {
            val table = tableInput.text.toString().toIntOrNull() ?: 0
            val people = peopleInput.text.toString().toIntOrNull() ?: 0
            val date = try { formatter.parse(dateInput.text.toString()) } catch (e: Exception) { null }

            if (date == null) {
                Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Use selectedGame if available, else fallback to manual text input
            val gameNameFinal = selectedGame?.name ?: gameNameInput.text.toString()

            selectedEvent?.let {
                val updated = it.copy(
                    gameName = gameNameFinal,
                    tableNumber = table,
                    numberOfPeople = people,
                    Date = date
                )
                CoroutineScope(Dispatchers.IO).launch {
                    eventDao.update(updated)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditEventActivity, "Event updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        deleteButton.setOnClickListener {
            selectedEvent?.let { event ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        CoroutineScope(Dispatchers.IO).launch {
                            eventDao.delete(event)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@EditEventActivity, "Event deleted", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    // Stub of your searchBoardGames suspend function — replace with your actual implementation
    private suspend fun searchBoardGames(query: String): List<Game> {
        // Your API call or database query here
        return emptyList()
    }
}
