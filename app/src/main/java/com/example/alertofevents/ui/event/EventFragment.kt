package com.example.alertofevents.ui.event

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alertofevents.R
import com.example.alertofevents.base.ui.BaseFragment
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.extension.setDisplayHomeAsUpEnabled
import com.example.alertofevents.common.extension.setTitle
import com.example.alertofevents.common.ui.MessageType
import com.example.alertofevents.databinding.EventFragmentBinding
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.ui.event.EventViewModel.Companion.DEFAULT_EVENT_ID
import com.example.alertofevents.ui.event.uiEvent.EventUiEvent
import com.github.ihermandev.formatwatcher.FormatWatcher
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class EventFragment : BaseFragment() {

    private lateinit var binding: EventFragmentBinding

    private val args by navArgs<EventFragmentArgs>()
    private val dateFormatWatcher by lazy {
        FormatWatcher(getString(R.string.dateMask),
            placeholderInFormat = '#')
    }

    override val viewModel: EventViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                EventViewModel.Factory> { factory ->
                    factory.create(args.eventId)
                }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EventFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.createEventFragmentTitle))
        setDisplayHomeAsUpEnabled(false)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch { viewModel.eventState.collect(::onViewState) }
                    launch { viewModel.uiEventFlow.collect(::onUiEvent) }
                }
            }
        }
    }

    private fun onViewState(state: LoadableData<Event>) {
        when (state) {
            is LoadableData.Success -> {
                viewModel.hideLoading()
                render(state.value)
            }
            is LoadableData.Loading -> viewModel.showLoading()
            is LoadableData.Error -> {
                viewModel.hideLoading()
                showMessageAsDialog(state.error.message, MessageType.ERROR)
            }
        }
    }

    private fun onUiEvent(uiEvent: EventUiEvent) {
        when (uiEvent) {
            is EventUiEvent.ClearFields -> {
                clearFields()
                showToast(SUCCESS_CREATE_EVENT_MSG)
            }
            is EventUiEvent.OpenCalendarOfEventsFragment -> {
                val day: LocalDate = uiEvent.date.toLocalDate()
                val month: YearMonth = YearMonth.from(day)
                val action = EventFragmentDirections.showCalendar(day.toString(), month.toString())
                findNavController().navigate(action)
            }
        }
    }

    private fun render(event: Event) {
        with(binding) {
            tietEventName.setText(event.name)
            tietDescription.setText(event.description)
            tietDate.setText(formatDateToString(event.date))
            tietDate.addTextChangedListener(dateFormatWatcher)
            scRemindMe.isChecked = event.remindMe

            if (event.id == DEFAULT_EVENT_ID) {
                btnEvent.text = CREATE_BTN_TEXT
            } else {
                btnEvent.text = SAVE_BTN_TEXT
            }

            btnEvent.setOnClickListener {
                insertOrUpdateEvent()
            }
        }
    }

    private fun insertOrUpdateEvent() {
        with(binding) {
            try {
                val event = Event(
                    name = tietEventName.text.toString(),
                    description = tietDescription.text.toString(),
                    date = formatStringToDate(tietDate.text.toString()),
                    remindMe = scRemindMe.isChecked
                )

                if (event.name.isNullOrBlank() || event.description.isNullOrBlank()) {
                    showMessageAsDialog(FILL_REQUIRED_FIELDS_MSG, MessageType.WARNING)
                } else {
                    viewModel.insertOrUpdateEvent(event)
                }

            } catch (error: Exception) {
                showMessageAsDialog(FILL_DATE_MSG, MessageType.WARNING)
            }
        }
    }

    private fun clearFields() {
        with(binding) {
            tietEventName.text?.clear()
            tietDescription.text?.clear()
            tietDate.setText(formatDateToString(LocalDateTime.now()))
            scRemindMe.isChecked = false
        }
    }

    private fun formatDateToString(date: LocalDateTime): String {
        val pattern = getString(R.string.datePattern)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
        return date.format(formatter)
    }

    private fun formatStringToDate(dateString: String): LocalDateTime {
        val pattern = getString(R.string.datePattern)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dateString, formatter)
    }

    companion object {
        private const val FILL_REQUIRED_FIELDS_MSG = "Be sure to fill in the \"name\" and \"description\" fields"
        private const val FILL_DATE_MSG = "Be sure to fill correct the date"
        private const val SUCCESS_CREATE_EVENT_MSG = "The event was successfully created"
        private const val CREATE_BTN_TEXT = "Create event"
        private const val SAVE_BTN_TEXT = "Save"
    }
}