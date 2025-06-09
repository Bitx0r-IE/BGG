package com.example.bgg.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bgg.Database.AppDatabase
import com.example.bgg.R
import com.example.bgg.auth.LoginActivity
import com.example.bgg.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonEdit: Button
    private lateinit var buttonLogout: Button
    private lateinit var userData: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        editUsername = root.findViewById(R.id.editUsername)
        editEmail = root.findViewById(R.id.editEmail)
        editPassword = root.findViewById(R.id.editPassword)
        buttonEdit = root.findViewById(R.id.buttonEdit)
        buttonLogout = root.findViewById(R.id.buttonLogout)
        userData = root.findViewById(R.id.idUserData)

        // Load user data from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("bgg_prefs", 0)
        val userId = sharedPref.getInt("userId", -1)

        val db = AppDatabase.getDatabase(requireContext())
        val userDao = db.userDao()

        // Load user data from database asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            val user = userDao.getUserById(userId)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    userData.text = user.name
                    editUsername.setText(user.name)
                    editEmail.setText(user.email)
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonEdit.setOnClickListener {
            val newUsername = editUsername.text.toString().trim()
            val newEmail = editEmail.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show confirmation dialog before updating
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Changes")
                .setMessage("Are you sure you want to update your profile?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    // Update user in database
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = userDao.getUserById(userId)
                        if (user != null) {
                            val updatedUser = user.copy(name = newUsername, email = newEmail)
                            userDao.updateUser(updatedUser)

                            // Optionally update SharedPreferences as well
                            with(sharedPref.edit()) {
                                putString("username", newUsername)
                                putString("email", newEmail)
                                apply()
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                                userData.text = newUsername
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        buttonLogout.setOnClickListener {
            // Clear shared preferences
            with(sharedPref.edit()) {
                clear()
                apply()
            }

            // Navigate to login screen and finish current activity
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}