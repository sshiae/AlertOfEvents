package com.example.alertofevents.common.extension

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertofevents.R
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalTime

/**
 * Used to set the title for a fragment.
 */
fun Fragment.setTitle(title: String) {
    (activity as AppCompatActivity).supportActionBar?.show()
    title.let {
        (activity as AppCompatActivity).supportActionBar?.title = it
    }
}

/**
 * Used to set the visibility of the "Up" button.
 */
fun Fragment.setDisplayHomeAsUpEnabled(enabled: Boolean) {
    val actionBar = (activity as? AppCompatActivity)?.supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(enabled)
}

/**
 * Get a hours of the HH type
 */
fun LocalTime.getHoursHH(): String {
    return String.format("%02d", hour)
}

/**
 * Get a minutes of the MM type
 */
fun LocalTime.getMinutesMM(): String {
    return String.format("%02d", minute)
}

/**
 * Used to set visibility
 */
fun View.makeVisible() {
    visibility = View.VISIBLE
}

/**
 * Used to set invisibility
 */
fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

/**
 * Used to disable/enable the input text
 */
fun TextInputEditText.setDisabled(enabled: Boolean) {
    isEnabled = enabled

    if (enabled) {
        setBackgroundColor(context.getColorCompat(R.color.colorAccent))
    } else {
        setBackgroundColor(context.getColorCompat(R.color.colorGray))
    }
}

/**
 * Used to get the color
 */
internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

/**
 * Used to set the color for the text
 */
internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))