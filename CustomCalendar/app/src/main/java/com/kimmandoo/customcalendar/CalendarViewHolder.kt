package com.kimmandoo.customcalendar

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kimmandoo.customcalendar.databinding.ItemDayBinding
import com.kimmandoo.customcalendar.model.CalendarItem

class CalendarViewHolder(
    private val binding: ItemDayBinding,
    private val onItemClick: (CalendarItem) -> Unit
) : ViewHolder(binding.root) {
    fun bind(data: CalendarItem) {
        binding.apply {
            tvDate.setOnClickListener {
                onItemClick(data)
            }
            tvDate.text = data.date.dayOfMonth.toString()
            if(data.isSunday) tvDate.setTextColor(Color.RED)
            tvDate.alpha = if (data.isCurrentMonth) 1f else 0.3f // 현재 달이 아니면 흐릿하게 해준다
        }
    }
}