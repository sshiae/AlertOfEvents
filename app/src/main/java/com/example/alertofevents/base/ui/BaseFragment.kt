package com.example.alertofevents.base.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.alertofevents.R
import com.example.alertofevents.common.ui.DialogButton
import com.example.alertofevents.common.ui.DialogData
import com.example.alertofevents.common.ui.MessageType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Base implementation of [Fragment].
 */
abstract class BaseFragment : Fragment() {

    /**
     * ViewModel
     */
    abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.firstLoad()
    }

    /**
     * Subscribe to [ViewModel] events.
     */
    protected open fun subscribeToViewModel() {
        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch { viewModel.loadingState.collect(::onLoadingStateChanged) }
                    launch { viewModel.dialogState.collect(::onDialogState) }
                    launch { viewModel.toastMessageState.collect(::onToastState) }
                }
            }
        }
    }

    /**
     * Show a message with the specified type.
     */
    protected open fun showMessageAsDialog(message: String?, type: MessageType) {
        val dialogData = DialogData(
            message = message ?: DEFAULT_MESSAGE,
            messageType = type,
            positiveButton = DialogButton(BTN_OK_TEXT) { }
        )
        showDialog(dialogData)
    }

    /**
     * Used to display Toast messages
     */
    protected open fun showToast(message: String?) {
        Toast.makeText(
            requireContext(),
            message ?: DEFAULT_MESSAGE,
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Used to display dialog
     */
    protected open fun showDialog(data: DialogData) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getTitleByType(data.messageType))
            .setMessage(data.message ?: DEFAULT_MESSAGE)
            .setIcon(getIconByType(data.messageType))

        if (data.positiveButton != null) {
            builder.setPositiveButton(data.positiveButton.text) { _, _ ->
                data.positiveButton.onClick.invoke()
            }
        } else {
            builder.setPositiveButton(BTN_OK_TEXT) { dlg, _ -> dlg.dismiss() }
        }

        data.negativeButton?.let { negativeButton ->
            builder.setNegativeButton(negativeButton.text) { _, _ ->
                negativeButton.onClick.invoke()
            }
        }

        data.neutralButton?.let { neutralButton ->
            builder.setNeutralButton(neutralButton.text) { _, _ ->
                neutralButton.onClick.invoke()
            }
        }

        builder.create().show()
    }

    private fun onDialogState(data: DialogData) {
        showDialog(data)
    }

    private fun onToastState(message: String) {
        showToast(message)
    }

    private fun onLoadingStateChanged(loading: Boolean) {
        requireView().findViewById<ConstraintLayout>(R.id.loading).isVisible = loading
    }

    private fun getIconByType(type: MessageType): Int {
        return when (type) {
            MessageType.ERROR -> R.drawable.ic_close
            MessageType.WARNING -> R.drawable.ic_warning
            MessageType.INFO -> 0
        }
    }

    private fun getTitleByType(type: MessageType): String {
        return when (type) {
            MessageType.ERROR -> ERROR_DIALOG_TITLE
            MessageType.WARNING -> WARNING_DIALOG_TITLE
            MessageType.INFO -> INFO_DIALOG_TITLE
        }
    }

    companion object {
        const val BTN_OK_TEXT = "OK"
        const val INFO_DIALOG_TITLE = "Message"
        const val ERROR_DIALOG_TITLE = "Error"
        const val WARNING_DIALOG_TITLE = "Warning"
        const val DEFAULT_MESSAGE = "An unexpected error has occurred"
    }
}