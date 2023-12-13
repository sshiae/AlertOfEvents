package com.example.alertofevents.ui.notification

import androidx.lifecycle.viewModelScope
import com.example.alertofevents.base.ui.BaseViewModel
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.emitError
import com.example.alertofevents.common.emitLoading
import com.example.alertofevents.common.emitSuccess
import com.example.alertofevents.domain.interactor.AlertOfEventsInteractor
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.domain.model.Settings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = NotificationViewModel.Factory::class)
class NotificationViewModel @AssistedInject constructor(
    private val interactor: AlertOfEventsInteractor,
    @Assisted private val eventId: Long
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long): NotificationViewModel
    }

    /**
     * [Flow] to get the event
     */
    private val viewStateFlow: MutableStateFlow<LoadableData<NotificationViewState>> =
        MutableStateFlow(LoadableData.Loading())
    val viewState = viewStateFlow.asStateFlow()

    override fun firstLoad() {
        viewModelScope.launch {
            try {
                if (eventId != DEFAULT_EVENT_ID) {
                    viewStateFlow.emitLoading()
                    val event: Event = interactor.getEventById(eventId)
                    val settings: Settings = interactor.getSettings()
                    val state = NotificationViewState(event, settings)
                    viewStateFlow.emitSuccess(state)
                }
            } catch (error: Exception) {
                viewStateFlow.emitError(error)
            }
        }
    }

    companion object {
        const val DEFAULT_EVENT_ID = -1L
    }
}