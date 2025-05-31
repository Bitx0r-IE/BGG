package com.example.bgg.ui.adapters

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bgg.Entities.EventEntity
import com.example.bgg.databinding.ActivityItemLayoutBinding

class EventAdapter : ListAdapter<EventEntity, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ActivityItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(private val binding: ActivityItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.title.text = event.gameName
            binding.date.text = event.Date.toString()
            // bind image or other data as needed
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<EventEntity>() {
        override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity) = oldItem == newItem
    }
}