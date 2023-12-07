package com.example.alertofevents.ui.calendarEvents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alertofevents.base.ui.BaseAdapter
import com.example.alertofevents.databinding.EventItemViewBinding
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.ui.calendarEvents.uiState.CalendarEventState

/**
 * Adapter for the event list
 */
class CalendarEventsAdapter(
    onItemClicked: (Event) -> Unit,
    onItemLongClicked: (Event) -> Unit
) : BaseAdapter<Event, CalendarEventState, CalendarEventsAdapter.ItemViewHolder>(
    createDiffCallback(
        { _, _ -> false },
        { oldItem, newItem -> oldItem == newItem }
    ),
    onItemClicked = onItemClicked,
    onItemLongClicked = onItemLongClicked
) {
    override fun createViewHolder(parent: ViewGroup): ItemViewHolder =
        ItemViewHolder(
            EventItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun bindViewHolder(holder: ItemViewHolder, item: CalendarEventState) =
        holder.bind(item)

    class ItemViewHolder(
        private val binding: EventItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: CalendarEventState) = with(binding) {
            tvEventDate.text = event.date
            tvEventText.text = event.name
        }
    }
}