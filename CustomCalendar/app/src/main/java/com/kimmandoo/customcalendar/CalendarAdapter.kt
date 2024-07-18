package com.kimmandoo.customcalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kimmandoo.customcalendar.base.BaseDiffUtil
import com.kimmandoo.customcalendar.databinding.ItemDayBinding
import com.kimmandoo.customcalendar.model.CalendarItem

class CalendarAdapter(private val onItemClick: (CalendarItem) -> Unit) :
    ListAdapter<CalendarItem, ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDayBinding.inflate(layoutInflater, parent, false)
        return CalendarViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CalendarViewHolder -> {
                holder.bind(currentList[position])
            }
        }
    }
    class CalendarItemDiffUtil : BaseDiffUtil<CalendarItem>()
    companion object {
        val diffUtil = CalendarItemDiffUtil()
    }
}