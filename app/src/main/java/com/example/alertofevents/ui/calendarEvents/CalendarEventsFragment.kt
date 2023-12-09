package com.example.alertofevents.ui.calendarEvents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alertofevents.R
import com.example.alertofevents.base.ui.BaseFragment
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.extension.makeInVisible
import com.example.alertofevents.common.extension.makeVisible
import com.example.alertofevents.common.extension.setDisplayHomeAsUpEnabled
import com.example.alertofevents.common.extension.setTextColorRes
import com.example.alertofevents.common.extension.setTitle
import com.example.alertofevents.common.ui.MessageType
import com.example.alertofevents.databinding.CalendarDayBinding
import com.example.alertofevents.databinding.CalendarEventsFragmentBinding
import com.example.alertofevents.databinding.CalendarHeaderBinding
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.ui.calendarEvents.uiEvent.CalendarEventsUiEvent
import com.example.alertofevents.ui.calendarEvents.uiState.CalendarEventState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class CalendarEventsFragment : BaseFragment() {

    private val titleSameYearFormatter by lazy {
        DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH)
    }
    private val titleFormatter by lazy {
        DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)
    }
    private val selectionFormatter by lazy {
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
    }
    private val titleForElementListFormatter by lazy {
        DateTimeFormatter.ofPattern("d MMM yyyy hh:mm:ss", Locale.ENGLISH)
    }
    private val today by lazy {
        LocalDate.now()
    }

    private val args by navArgs<CalendarEventsFragmentArgs>()

    private lateinit var binding: CalendarEventsFragmentBinding
    private lateinit var adapter: CalendarEventsAdapter

    override val viewModel: CalendarEventsViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                CalendarEventsViewModel.Factory> { factory ->
                val month: YearMonth = if (args.month.isNullOrEmpty())
                    YearMonth.now() else YearMonth.parse(args.month)
                val day: LocalDate = if (args.day.isNullOrEmpty())
                    month.atDay(1) else LocalDate.parse(args.day)
                factory.create(month, day)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CalendarEventsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindAdapter()
        setupCalendar()
        setTitle(getString(R.string.calendarEventsFragmentTitle))
        setDisplayHomeAsUpEnabled(false)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch { viewModel.existenceEventsByDayFlow.collect(::onExistenceEventsByDayFlowState) }
                    launch { viewModel.selectedEventsState.collect(::onSelectedEventsState) }
                    launch { viewModel.uiEventState.collect(::onUiEvent) }
                    launch { viewModel.selectedDayFlow.collect(::onDaySelectedState) }
                    launch { viewModel.selectedMonthFlow.collect(::onMonthSelectedState) }
                }
            }
        }
    }

    private fun onExistenceEventsByDayFlowState(state: LoadableData<Map<Int, Boolean>>) {
        when (state) {
            is LoadableData.Success -> {
                viewModel.hideLoading()
                binding.cvCalendar.notifyMonthChanged(viewModel.selectedMonthFlow.value)
            }
            is LoadableData.Loading -> viewModel.showLoading()
            is LoadableData.Error -> {
                viewModel.hideLoading()
                showMessageAsDialog(state.error.message, MessageType.ERROR)
            }
        }
    }

    private fun onSelectedEventsState(state: LoadableData<List<Event>>) {
        when (state) {
            is LoadableData.Success -> {
                val events = state.value
                if (events.isEmpty()) {
                    showPlug()
                } else {
                    hidePlug()
                    val mappedValue = events.map { mapToEventState(it) }
                    adapter.submitList(mappedValue)
                }
                viewModel.hideLoading()
            }
            is LoadableData.Loading -> viewModel.showLoading()
            is LoadableData.Error -> {
                showPlug()
                viewModel.hideLoading()
                showMessageAsDialog(state.error.message, MessageType.ERROR)
            }
        }
    }

    private fun onDaySelectedState(day: LocalDate) {
        with(binding) {
            cvCalendar.notifyDateChanged(day)
            tvSelectedDate.text = selectionFormatter.format(day)
        }
    }

    private fun onMonthSelectedState(month: YearMonth) {
        with(binding) {
            cvCalendar.notifyMonthChanged(month)
        }
    }

    private fun onUiEvent(uiEvent: CalendarEventsUiEvent) {
        when (uiEvent) {
            is CalendarEventsUiEvent.OpenEventForEdit -> {
                val action = CalendarEventsFragmentDirections.editEvent(uiEvent.eventId)
                findNavController().navigate(action)
            }
        }
    }

    private fun mapToEventState(event: Event): CalendarEventState {
        return CalendarEventState(
            name = event.name!!,
            date = event.date.format(titleForElementListFormatter),
            original = event
        )
    }

    private fun bindAdapter() {
        with(binding) {
            adapter = CalendarEventsAdapter(
                onItemClicked = viewModel::onItemClicked,
                onItemLongClicked = viewModel::onItemLongClicked
            )
            rvEvents.adapter = adapter
            rvEvents.layoutManager = LinearLayoutManager(rvEvents.context)
        }
    }

    private fun setupCalendar() {
        with(binding) {
            val daysOfWeek = daysOfWeek()
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(50)
            val endMonth = currentMonth.plusMonths(50)
            configureBinders(daysOfWeek)
            cvCalendar.apply {
                monthScrollListener = {
                    viewModel.selectMonth(it.yearMonth)
                    val title = if (it.yearMonth.year == today.year) {
                        titleSameYearFormatter.format(it.yearMonth)
                    } else {
                        titleFormatter.format(it.yearMonth)
                    }
                    setTitle(title)
                }
                setup(startMonth, endMonth, daysOfWeek.first())
                scrollToMonth(viewModel.selectedMonthFlow.value)
                selectDate(viewModel.selectedDayFlow.value)
            }
        }
    }

    private fun showPlug() {
        binding.plug.isVisible = true
    }

    private fun hidePlug() {
        binding.plug.isVisible = false
    }

    private fun selectDate(day: LocalDate) {
        val selectedDate: LocalDate = viewModel.selectedDayFlow.value
        binding.cvCalendar.notifyDateChanged(selectedDate)
        viewModel.selectDay(day)
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        with(binding.cvCalendar) {
            dayBinder = createMonthDayBinder()
            monthHeaderBinder = createMonthHeaderFooterBinder(daysOfWeek)
        }
    }

    private fun createMonthDayBinder(): MonthDayBinder<DayViewContainer> {
        return object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view, ::selectDate)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.tvDayText
                val dotView = container.binding.dotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.makeVisible()
                    when (data.date) {
                        today -> {
                            textView.setTextColorRes(R.color.colorWhite)
                            textView.setBackgroundResource(R.drawable.today_bg)
                            dotView.makeInVisible()
                        }
                        viewModel.selectedDayFlow.value -> {
                            textView.setTextColorRes(R.color.colorAccent)
                            textView.setBackgroundResource(R.drawable.selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.colorText)
                            textView.background = null
                            val loadableData: LoadableData<Map<Int, Boolean>> =
                                viewModel.existenceEventsByDayFlow.value
                            if (loadableData.hasValue) {
                                dotView.isVisible =
                                    loadableData.value!!.containsKey(data.date.dayOfMonth)
                            } else {
                                dotView.isVisible = false
                            }
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }
    }

    private fun createMonthHeaderFooterBinder(
        daysOfWeek: List<DayOfWeek>
    ): MonthHeaderFooterBinder<MonthViewContainer> {
        return object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = data.yearMonth
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text = daysOfWeek[index].name.first().toString()
                            tv.setTextColorRes(R.color.colorText)
                        }
                }
            }
        }
    }

    /**
     * A container representing a day in the calendar
     */
    private class DayViewContainer(
        view: View,
        selectDate: (LocalDate) -> Unit
    ) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val binding = CalendarDayBinding.bind(view)

        init {
            view.setOnClickListener {
                if (day.position == DayPosition.MonthDate) {
                    selectDate(day.date)
                }
            }
        }
    }

    /**
     * A container representing a month in the calendar
     */
    private class MonthViewContainer(
        view: View
    ) : ViewContainer(view) {
        val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
    }
}