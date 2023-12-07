package com.example.alertofevents.base.ui

import androidx.lifecycle.ViewModel
import com.example.alertofevents.common.ui.DialogData
import com.example.alertofevents.common.ui.MessageType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Base implementation of [ViewModel].
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * [Flow] for the loading indicator.
     */
    private val loadingStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(DEFAULT_LOADING_STATE)
    val loadingState = loadingStateFlow.asStateFlow()

    /**
     * [Channel] for the message in the dialog
     */
    private val dialogChannel: Channel<DialogData> = Channel(Channel.BUFFERED)
    val dialogState = dialogChannel.receiveAsFlow()

    /**
     * [Channel] for the message in the toast
     */
    private val toastMessageChannel: Channel<String> = Channel(Channel.BUFFERED)
    val toastMessageState = toastMessageChannel.receiveAsFlow()

    /**
     * Show loading state.
     */
    fun showLoading() {
        loadingStateFlow.update { true }
    }

    /**
     * Hide loading state.
     */
    fun hideLoading() {
        loadingStateFlow.update { false }
    }

    /**
     * Used to display dialog
     */
    fun showDialog(data: DialogData) {
        dialogChannel.trySend(data)
    }

    /**
     * Used to display error dialog
     */
    fun showErrorDialog(message: String?) {
        val dialogData = DialogData(
            message = message,
            messageType = MessageType.ERROR
        )
        showDialog(dialogData)
    }

    /**
     * Used to display a message as a toast
     */
    fun showToast(message: String) {
        toastMessageChannel.trySend(message)
    }

    /**
     * Initial data loading.
     */
    abstract fun firstLoad()

    companion object {
        const val DEFAULT_LOADING_STATE = true
    }
}