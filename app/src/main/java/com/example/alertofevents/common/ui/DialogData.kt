package com.example.alertofevents.common.ui

/**
 * Represents the data for the dialog
 */
data class DialogData(
    val message: String?,
    val messageType: MessageType = MessageType.INFO,
    val positiveButton: DialogButton? = null,
    val negativeButton: DialogButton? = null,
    val neutralButton: DialogButton? = null
)

/**
 * Represents the button for the dialog
 */
data class DialogButton(
    val text: String,
    val onClick: () -> Unit
)

/**
 * Message Types
 */
enum class MessageType {
    ERROR,
    INFO,
    WARNING
}