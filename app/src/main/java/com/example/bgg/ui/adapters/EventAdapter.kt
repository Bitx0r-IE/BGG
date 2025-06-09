package com.example.bgg.ui.adapters

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bgg.Entities.EventEntity
import com.example.bgg.databinding.ActivityItemLayoutBinding

class EventAdapter(private val onItemClick: (EventEntity) -> Unit) : ListAdapter<EventEntity, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ActivityItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(private val binding: ActivityItemLayoutBinding, private val onItemClick: (EventEntity) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            val formattedDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(event.Date)
            binding.title.text = event.gameName
            binding.date.text = formattedDate
            // bind image or other data as needed
            binding.root.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<EventEntity>() {
        override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity) = oldItem == newItem
    }
}