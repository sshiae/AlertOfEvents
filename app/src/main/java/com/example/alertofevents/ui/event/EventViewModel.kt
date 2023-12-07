package com.example.alertofevents.ui.event

import androidx.lifecycle.viewModelScope
import com.example.alertofevents.base.ui.BaseViewModel
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.emitError
import com.example.alertofevents.common.emitLoading
import com.example.alertofevents.common.emitSuccess
import com.example.alertofevents.domain.interactor.AlertOfEventsInteractor
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.ui.event.uiEvent.EventUiEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for working with events
 */
@HiltViewModel(assistedFactory = EventViewModel.Factory::class)
class EventViewModel @AssistedInject constructor(
    private val interactor: AlertOfEventsInteractor,
    @Assisted private val eventId: Long
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long): EventViewModel
    }

    /**
     * [Flow] to get the event
     */
    private val eventStateFlow: MutableStateFlow<LoadableData<Event>> =
        MutableStateFlow(LoadableData.Loading())
    val eventState = eventStateFlow.asStateFlow()

    /**
     * [Channel] for UI events
     */
    private val uiEventChannel: Channel<EventUiEvent> = Channel(Channel.BUFFERED)
    val uiEventFlow = uiEventChannel.receiveAsFlow()

    override fun firstLoad() {
        viewModelScope.launch {
            try {
                eventStateFlow.emitLoading()
                if (eventId != DEFAULT_EVENT_ID) {
                    val foundEvent = interactor.getEventById(eventId)
                    eventStateFlow.emitSuccess(foundEvent)
                } else {
                    eventStateFlow.emitSuccess(Event(id = DEFAULT_EVENT_ID))
                }
            } catch (error: Exception) {
                eventStateFlow.emitError(error)
            }
        }
    }

    /**
     * Used to insert or update an event
     */
    fun insertOrUpdateEvent(event: Event) {
        viewModelScope.launch {
            try {
                showLoading()
                if (eventId != DEFAULT_EVENT_ID) {
                    interactor.insertOrUpdateEvent(event.copy(id = eventId))
                    uiEventChannel.trySend(EventUiEvent.OpenCalendarOfEventsFragment(event.date))
                } else {
                    interactor.insertOrUpdateEvent(event.copy(id = null))
                    uiEventChannel.trySend(EventUiEvent.ClearFields)
                }
            } catch (error: Exception) {
                showErrorDialog(error.message)
            } finally {
                hideLoading()
            }
        }
    }

    companion object {
        const val DEFAULT_EVENT_ID = -1L
    }
}