package com.example.alertofevents.ui.main

import androidx.lifecycle.viewModelScope
import com.example.alertofevents.base.ui.BaseViewModel
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.emitError
import com.example.alertofevents.common.emitLoading
import com.example.alertofevents.common.emitSuccess
import com.example.alertofevents.domain.interactor.AlertOfEventsInteractor
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MainViewModel.Factory::class)
class MainViewModel @AssistedInject constructor(
    private val interactor: AlertOfEventsInteractor
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): MainViewModel
    }

    /**
     * [Flow] to get an indication whether the notification service has been started
     */
    private val viewStateFlow: MutableStateFlow<LoadableData<Boolean>> =
        MutableStateFlow(LoadableData.Loading())
    val viewState = viewStateFlow.asStateFlow()

    override fun firstLoad() {
        viewModelScope.launch {
            try {
                viewStateFlow.emitLoading()
                viewStateFlow.emitSuccess(interactor.isWorkerScheduled())
            } catch (error: Exception) {
                viewStateFlow.emitError(error)
            }
        }
    }

    /**
     * To set the indicator of the neglect of the notification service
     */
    fun setIsWorkerScheduled(workerScheduled: Boolean) {
        viewModelScope.launch {
            interactor.setIsWorkerScheduled(workerScheduled)
        }
    }
}