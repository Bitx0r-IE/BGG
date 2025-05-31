package com.example.bgg.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bgg.databinding.FragmentDashboardBinding
import com.example.bgg.databinding.FragmentHomeBinding
import com.example.bgg.ui.CreateEventActivity
import com.example.bgg.ui.adapters.EventAdapter
import com.example.bgg.ui.home.HomeViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d("HomeFragment", "Setting layoutManager and adapter")
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())

        adapter = EventAdapter()
        binding.recyclerViewEvents.adapter = adapter
        Log.d("HomeFragment", "Adapter set")
        homeViewModel.events.observe(viewLifecycleOwner) { events ->
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