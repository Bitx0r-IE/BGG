package com.example.bgg.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bgg.databinding.FragmentHomeBinding
import com.example.bgg.ui.adapters.EventAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bgg.ui.event.ViewEventActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("HomeFragment", "Setting layoutManager and adapter")
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())

        adapter = EventAdapter { selectedEvent ->
            val intent = Intent(requireContext(), ViewEventActivity::class.java)
            intent.putExtra("eventId", selectedEvent.id)
            startActivity(intent)
        }

        binding.recyclerViewEvents.adapter = adapter
        Log.d("HomeFragment", "Adapter set")
        homeViewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}