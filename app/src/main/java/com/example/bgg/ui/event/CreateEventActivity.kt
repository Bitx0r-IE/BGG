package com.example.bgg.ui.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bgg.DAO.EventDao
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.EventEntity
import com.example.bgg.R
import com.example.bgg.api.Game
import com.example.bgg.api.searchBoardGames
import com.example.bgg.api.searchGameDetailsByID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

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

        val gameSearch = findViewById<AutoCompleteTextView>(R.id.game_search)
        val tableInput = findViewById<EditText>(R.id.table_number)
        val peopleInput = findViewById<EditText>(R.id.number_of_people)
        val createButton = findViewById<Button>(R.id.create_event_button)
        val eventDateEditText = findViewById<EditText>(R.id.event_date)

        //val gameImage = findViewById<ImageView>(R.id.game_image)
        //val gameDescription = findViewById<TextView>(R.id.game_description)

        val gameAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        val gameMap = mutableMapOf<String, Game>()

        gameSearch.setAdapter(gameAdapter)

        gameSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.length >= 3) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val games = searchBoardGames(query)
                        withContext(Dispatchers.Main) {
                            gameAdapter.clear()
                            gameMap.clear()
                            games.forEach {
                                gameAdapter.add(it.name)
                                gameMap[it.name] = it
                            }
                            gameAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        gameSearch.setOnItemClickListener { _, _, position, _ ->
            val selectedName = gameAdapter.getItem(position)
            selectedGame = gameMap[selectedName]

            // Fetch details to get image and description
            selectedGame?.let { game ->
                CoroutineScope(Dispatchers.IO).launch {
                    val detail = searchGameDetailsByID(game.id)
                    withContext(Dispatchers.Main) {
                        /*if (detail != null) {
                            gameDescription.text = detail.desc
                            gameDescription.visibility = TextView.VISIBLE
                            gameImage.visibility = ImageView.VISIBLE
                            Glide.with(this@CreateEventActivity)
                                .load(detail.img)
                                .centerCrop()
                                .into(gameImage)
                        } else {
                            gameDescription.visibility = TextView.GONE
                            gameImage.visibility = ImageView.GONE
                        }*/
                    }
                }
            }
        }

        eventDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
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

            val gameName = selectedGame?.name ?: gameSearch.text.toString()

            val newEvent = EventEntity(
                gameName = gameName,
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
        }
    }
}
