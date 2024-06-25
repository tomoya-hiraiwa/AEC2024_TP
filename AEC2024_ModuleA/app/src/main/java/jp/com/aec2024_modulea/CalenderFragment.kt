package jp.com.aec2024_modulea

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.com.aec2024_modulea.databinding.CalenderItemBinding
import jp.com.aec2024_modulea.databinding.FragmentCalenderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


class CalenderFragment : Fragment() {
    private lateinit var b: FragmentCalenderBinding
    private var dateList = mutableListOf<Pair<String, String>>()
    private lateinit var calenderAdapter: CalenderAdapter
    private var fromX = 0f
    private var toX = 0f
    var year = 2024
    var month = 5
    private val startDayOfWeek = DayOfWeek.SUNDAY
    private val endDayOfWeek = DayOfWeek.SATURDAY
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentCalenderBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.apply {
            calender.layoutManager = GridLayoutManager(requireContext(), 7)
            calenderAdapter = CalenderAdapter(dateList) { item ->
                eventDetailData = item
                val manager = requireParentFragment().parentFragmentManager
                manager.beginTransaction()
                    .add(R.id.fragmentContainerView, EventDetailFragment())
                    .commit()
            }
            calender.adapter = calenderAdapter
            calender.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        fromX = event.x
                        false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        toX = event.x
                        val subX = toX - fromX
                        if (subX < 200f && subX > -200f) {
                            backArrow.isVisible = false
                            frontArrow.isVisible = false
                        } else if (subX >= 200f) {
                            backArrow.isVisible = true
                        } else if (subX <= -200f) {
                            frontArrow.isVisible = true

                        }
                        false
                    }

                    MotionEvent.ACTION_UP -> {
                        if (backArrow.visibility == View.VISIBLE) {
                            month -= 1
                            getDates(year, month, startDayOfWeek, endDayOfWeek)
                            calenderAdapter.notifyDataSetChanged()
                            frontArrow.isVisible = false
                            backArrow.isVisible = false

                        } else if (frontArrow.visibility == View.VISIBLE) {
                            month += 1
                            getDates(year, month, startDayOfWeek, endDayOfWeek)
                            calenderAdapter.notifyDataSetChanged()
                            frontArrow.isVisible = false
                            backArrow.isVisible = false

                        } else {
                            false
                        }
                        false
                    }

                    else -> false
                }
            }
            getDates(year, month, startDayOfWeek, endDayOfWeek)
            calenderAdapter.notifyDataSetChanged()

        }
    }

    private fun getDates(
        year: Int,
        month: Int,
        startDayOfWeek: DayOfWeek,
        endDayOfWeek: DayOfWeek
    ) {
        dateList.clear()
        val valueDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startFormatter = DateTimeFormatter.ofPattern("MMM d")
        val normalFormatter = DateTimeFormatter.ofPattern("d")
        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth())
        val firstDoW = firstDay.with(TemporalAdjusters.previousOrSame(startDayOfWeek))
        val lastDoW = lastDay.with(TemporalAdjusters.nextOrSame(endDayOfWeek))

        var currentDate = firstDoW
        while (!currentDate.isAfter(lastDoW)) {
            val date = valueDateFormatter.format(currentDate)
            val showDate = if (currentDate.dayOfMonth == 1) {
                startFormatter.format(currentDate)
            } else normalFormatter.format(currentDate)
            dateList.add(Pair(date, showDate))
            currentDate = currentDate.plusDays(1)
        }
    }
}

class CalenderAdapter(
    private val dateList: MutableList<Pair<String, String>>,
    val onClick: (Pair<String, EventData>) -> Unit
) : RecyclerView.Adapter<CalenderAdapter.CalenderViewHolder>() {
    inner class CalenderViewHolder(private val b: CalenderItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        @SuppressLint("Range")
        fun bindData(data: Pair<String, String>) {
            println(data.first)
            b.apply {
                val eventData = allData.events.toList().firstOrNull { it.second.date == data.first }
                if (eventData != null) {
                    eventFrame.apply {
                        setCardBackgroundColor(Color.parseColor(eventData.second.color))
                        isVisible = true
                    }
                    eventName.text = eventData.second.name
                    eventFrame.setOnClickListener {
                        onClick(eventData)
                    }
                } else eventFrame.isVisible = false
                dayText.text = data.second
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderViewHolder {
        return CalenderViewHolder(
            CalenderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(holder: CalenderViewHolder, position: Int) {
        holder.bindData(dateList[position])
    }
}