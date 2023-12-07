package com.example.alertofevents.ui.calendarEvents

import androidx.lifecycle.viewModelScope
import com.example.alertofevents.base.ui.BaseViewModel
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.emitError
import com.example.alertofevents.common.emitLoading
import com.example.alertofevents.common.emitSuccess
import com.example.alertofevents.common.ui.DialogButton
import com.example.alertofevents.common.ui.DialogData
import com.example.alertofevents.domain.interactor.AlertOfEventsInteractor
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.ui.calendarEvents.uiEvent.CalendarEventsUiEvent
import com.example.alertofevents.ui.event.EventViewModel.Companion.DEFAULT_EVENT_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * ViewModel for working with calendar events
 */
@HiltViewModel(assistedFactory = CalendarEventsViewModel.Factory::class)
class CalendarEventsViewModel @AssistedInject constructor(
    private val interactor: AlertOfEventsInteractor,
    @Assisted private val month: YearMonth,
    @Assisted private val day: LocalDate
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(month: YearMonth, day: LocalDate): CalendarEventsViewModel
    }

    /**
     * [Flow] for existence events by day
     */
    private val existenceEventsByDayStateFlow: MutableStateFlow<LoadableData<Map<Int, Boolean>>> =
        MutableStateFlow(LoadableData.Loading())
    val existenceEventsByDayFlow = existenceEventsByDayStateFlow.asStateFlow()

    /**
     * [Flow] for selected month
     */
    private val selectedMonthStateFlow: MutableStateFlow<YearMonth> =
        MutableStateFlow(month)
    val selectedMonthFlow = selectedMonthStateFlow.asStateFlow()

    /**
     * [Flow] for selected day
     */
    private val selectedDayStateFlow: MutableStateFlow<LocalDate> =
        MutableStateFlow(day)
    val selectedDayFlow = selectedDayStateFlow.asStateFlow()

    /**
     * [Flow] for selected event
     */
    private val selectedEventsStateFlow: MutableStateFlow<LoadableData<List<Event>>> =
        MutableStateFlow(LoadableData.Loading())
    val selectedEventsState = selectedEventsStateFlow.asStateFlow()

    /**
     * [Channel] for send UI events
     */
    private val uiEventChannel: Channel<CalendarEventsUiEvent> = Channel(Channel.BUFFERED)
    val uiEventState = uiEventChannel.receiveAsFlow()

    override fun firstLoad() {
        viewModelScope.launch {
            try {
                existenceEventsByDayStateFlow.emitLoading()
                interactor.getExistenceEventsByDay(YearMonth.now()).collect {
                    existenceEventsByDayStateFlow.emitSuccess(it)
                }
            } catch (error: Exception) {
                existenceEventsByDayStateFlow.emitError(error)
            }
        }
    }

    /**
     * Used to select events by day
     */
    fun selectDay(day: LocalDate) {
        viewModelScope.launch {
            try {
                selectedEventsStateFlow.emitLoading()
                selectedDayStateFlow.update { day }
                selectedEventsStateFlow.emitSuccess(interactor.getEventsByDay(day))
            } catch (error: Exception) {
                selectedEventsStateFlow.emitError(error)
            }
        }
    }

    /**
     * Used to select month
     */
    fun selectMonth(month: YearMonth) {
        viewModelScope.launch {
            try {
                existenceEventsByDayStateFlow.emitLoading()
                selectedMonthStateFlow.update { month }
                interactor.getExistenceEventsByDay(month).collect {
                    existenceEventsByDayStateFlow.emitSuccess(it)
                }
            } catch (error: Exception) {
                existenceEventsByDayStateFlow.emitError(error)
            }
        }
    }

    /**
     * It is used when an element [event] is clicked for a long time
     */
    fun onItemLongClicked(event: Event) {
        val dialogData = DialogData(
            message = DELETE_ITEM_MSG,
            positiveButton = DialogButton(YES_BTN) { deleteEvent(event) },
            negativeButton = DialogButton(NO_BTN) { }
        )
        showDialog(dialogData)
    }

    /**
     * It is used when an element [event] is clicked
     */
    fun onItemClicked(event: Event) {
        uiEventChannel.trySend(CalendarEventsUiEvent.OpenEventForEdit(event.id ?: DEFAULT_EVENT_ID))
    }

    private fun deleteEvent(event: Event) {
        viewModelScope.launch {
            try {
                selectedEventsStateFlow.emitLoading()
                interactor.deleteEvent(event)
                val foundEventsByDay = interactor.getEventsByDay(event.date.toLocalDate()!!)
                selectedEventsStateFlow.emitSuccess(foundEventsByDay)
            } catch (error: Exception) {
                selectedEventsStateFlow.emitError(error)
            }
        }
    }

    companion object {
        const val DELETE_ITEM_MSG = "Delete an event?"
        const val YES_BTN = "Yes"
        const val NO_BTN = "No"
    }
}