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
import com.example.bgg.R
import com.example.bgg.auth.LoginActivity
import com.example.bgg.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var buttonEdit: Button
    private lateinit var buttonLogout: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        editUsername = root.findViewById(R.id.editUsername)
        editEmail = root.findViewById(R.id.editEmail)
        buttonEdit = root.findViewById(R.id.buttonEdit)
        buttonLogout = root.findViewById(R.id.buttonLogout)

        // Load user data from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("bgg_prefs", 0)
        val username = sharedPref.getString("username", "") ?: ""
        val email = sharedPref.getString("email", "") ?: ""

        editUsername.setText(username)
        editEmail.setText(email)

        buttonEdit.setOnClickListener {
            val newUsername = editUsername.text.toString().trim()
            val newEmail = editEmail.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save updated info in SharedPreferences
            with(sharedPref.edit()) {
                putString("username", newUsername)
                putString("email", newEmail)
                apply()
            }

            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
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