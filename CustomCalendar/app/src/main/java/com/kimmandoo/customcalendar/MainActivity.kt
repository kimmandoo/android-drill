package com.kimmandoo.customcalendar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kimmandoo.customcalendar.databinding.ActivityMainBinding
import com.kimmandoo.customcalendar.model.CalendarItem
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val calendarAdapter by lazy {
        CalendarAdapter(onItemClick = { item ->
            Toast.makeText(this, "${item.date}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, ": $item")
            showOnlySelectedWeek(item.date)
        })
    }
    private var selectedDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initScreen()
        binding.apply {
            rvCalendar.apply {
                tvMonthAndYear.text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                adapter = calendarAdapter
                calendarAdapter.submitList(calculateCalendar(selectedDate))
            }
            back.setOnClickListener {
                selectedDate = selectedDate.minusMonths(1)
                tvMonthAndYear.text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                calendarAdapter.submitList(calculateCalendar(selectedDate))
            }
            front.setOnClickListener {
                selectedDate = selectedDate.plusMonths(1)
                tvMonthAndYear.text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                calendarAdapter.submitList(calculateCalendar(selectedDate))
            }
        }
    }

    private fun initScreen() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun findStartOfWeek(date: LocalDate): LocalDate {
        var current = date
        while (current.dayOfWeek != DayOfWeek.SUNDAY) {
            current = current.minusDays(1)
        }
        return current
    }

    private fun showOnlySelectedWeek(selectedDate: LocalDate) {
        val startOfWeek = findStartOfWeek(selectedDate)
        val endOfWeek = startOfWeek.plusDays(6)

        val updatedList = calendarAdapter.currentList.filter { item ->
            item.date >= startOfWeek && item.date <= endOfWeek
        }
        calendarAdapter.submitList(updatedList)
    }

    private fun calculateCalendar(date: LocalDate): List<CalendarItem> {
        val days = mutableListOf<CalendarItem>()
        val yearMonth = YearMonth.from(date)
        val firstOfMonth = yearMonth.atDay(1)
        val daysInMonth = yearMonth.lengthOfMonth()

        // 이전 달의 날짜 추가
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        val previousMonth = yearMonth.minusMonths(1)
        val previousMonthDays = previousMonth.lengthOfMonth()
        for (i in 0 until dayOfWeek) {
            val currentDate = previousMonth.atDay(previousMonthDays - dayOfWeek + i + 1)
            days.add(
                CalendarItem(
                    date = currentDate,
                    isSunday = currentDate.dayOfWeek == DayOfWeek.SUNDAY,
                    isCurrentMonth = false
                )
            )
        }

        // 현재 달의 날짜 추가
        for (i in 1..daysInMonth) {
            val currentDate = firstOfMonth.plusDays(i - 1L)
            days.add(
                CalendarItem(
                    date = currentDate,
                    isSunday = currentDate.dayOfWeek == DayOfWeek.SUNDAY,
                    isCurrentMonth = true
                )
            )
        }

        // 다음 달의 날짜 추가
        val nextMonth = yearMonth.plusMonths(1)
        for (i in 1 until 43 - (dayOfWeek + daysInMonth)) {
            val currentDate = nextMonth.atDay(i)
            days.add(
                CalendarItem(
                    date = currentDate,
                    isSunday = currentDate.dayOfWeek == DayOfWeek.SUNDAY,
                    isCurrentMonth = false
                )
            )
        }

        return days
    }
}