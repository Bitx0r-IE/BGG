package com.example.bgg.ui.event
import java.util.Calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bgg.DAO.EventDao
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.EventEntity
import com.example.bgg.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Locale


class EditEventActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var eventDao: EventDao
    private var selectedEvent: EventEntity? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        db = AppDatabase.getDatabase(this)
        eventDao = db.eventDao()

        val gameNameInput = findViewById<EditText>(R.id.game_search)
        val tableInput = findViewById<EditText>(R.id.table_number)
        val peopleInput = findViewById<EditText>(R.id.number_of_people)
        val dateInput = findViewById<EditText>(R.id.event_date)
        val updateButton = findViewById<Button>(R.id.create_event_button)
        val deleteButton = findViewById<Button>(R.id.delete_event_button)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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
            val gameName = gameNameInput.text.toString()
            val date = try { formatter.parse(dateInput.text.toString()) } catch (e: Exception) { null }

            if (date == null) {
                Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedEvent?.let {
                val updated = it.copy(
                    gameName = gameName,
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
                CoroutineScope(Dispatchers.IO).launch {
                    eventDao.delete(event)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditEventActivity, "Event deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}


