package com.example.bgg.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bgg.databinding.FragmentDashboardBinding
import com.example.bgg.ui.event.CreateEventActivity
import com.example.bgg.ui.event.EditEventActivity
import com.example.bgg.ui.adapters.EventAdapter
import com.example.bgg.ui.home.HomeViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: EventAdapter
    private val dashboardViewModel: DashboardViewModel by lazy {
        ViewModelProvider(this, DashboardViewModelFactory(requireActivity().application))[DashboardViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())

        adapter = EventAdapter { selectedEvent ->
            val intent = Intent(requireContext(), EditEventActivity::class.java)
            intent.putExtra("eventId", selectedEvent.id)
            startActivity(intent)
        }

        binding.recyclerViewEvents.adapter = adapter

        dashboardViewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        binding.buttonCreateEvent.setOnClickListener {
            val intent = Intent(requireContext(), CreateEventActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}