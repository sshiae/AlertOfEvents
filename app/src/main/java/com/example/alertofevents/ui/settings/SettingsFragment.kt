package com.example.alertofevents.ui.settings

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.alertofevents.R
import com.example.alertofevents.base.ui.BaseFragment
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.extension.getHoursHH
import com.example.alertofevents.common.extension.getMinutesMM
import com.example.alertofevents.common.extension.setDisabled
import com.example.alertofevents.common.extension.setDisplayHomeAsUpEnabled
import com.example.alertofevents.common.extension.setTitle
import com.example.alertofevents.common.ui.MessageType
import com.example.alertofevents.databinding.SettingsFragmentBinding
import com.example.alertofevents.domain.model.Settings
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import java.time.LocalTime

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private lateinit var binding: SettingsFragmentBinding

    private var mediaPlayer: MediaPlayer? = null

    private val hoursTextWatcher by lazy {
        createTextWatcherForTime(MAX_HOURS_NUMBER)
    }
    private val minutesTextWatcher by lazy {
        createTextWatcherForTime(MAX_MINUTES_NUMBER)
    }

    override val viewModel: SettingsViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                SettingsViewModel.Factory> { factory ->
                factory.create()
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        stopPlaying()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.settingsFragmentTitle))
        setDisplayHomeAsUpEnabled(false)
        setupSoundsDropdown()
        setupViews()
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch { viewModel.settingsState.collect(::onViewState) }
                }
            }
        }
    }

    private fun onViewState(state: LoadableData<Settings>) {
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

    private fun setupViews() {
        with(binding) {
            etAlertBeforeFirstTimeHours.addTextChangedListener(hoursTextWatcher)
            etAlertBeforeFirstTimeMinutes.addTextChangedListener(minutesTextWatcher)
            etIntervalTimeStopHours.addTextChangedListener(hoursTextWatcher)
            etIntervalTimeStopMinutes.addTextChangedListener(minutesTextWatcher)
            cbIntervalTimeStop.setOnClickListener { setupTimeFieldsByCheckBox() }
            btnSaveSettings.setOnClickListener { onClickSaveButton() }
        }
    }

    private fun render(settings: Settings) {
        with(binding) {
            etAlertBeforeFirstTimeHours.setText(settings.firstTimeToStart.getHoursHH())
            etAlertBeforeFirstTimeMinutes.setText(settings.firstTimeToStart.getMinutesMM())
            etIntervalTimeStopHours.setText(settings.timeForStopAlerting.getHoursHH())
            etIntervalTimeStopMinutes.setText(settings.timeForStopAlerting.getMinutesMM())
            cbIntervalTimeStop.isChecked = settings.timeForStopAlertingEnabled
            actvSound.setText(settings.soundName, false)
            setupTimeFieldsByCheckBox()
        }
    }

    private fun setupTimeFieldsByCheckBox() {
        with(binding) {
            etIntervalTimeStopHours.setDisabled(cbIntervalTimeStop.isChecked)
            etIntervalTimeStopMinutes.setDisabled(cbIntervalTimeStop.isChecked)
        }
    }

    private fun onClickSaveButton() {
        with(binding) {
            try {
                if (checkErrors()) {
                    return
                }

                val firstTimeToStartHours = etAlertBeforeFirstTimeHours.text.toString()
                val firstTimeToStartMinutes = etAlertBeforeFirstTimeMinutes.text.toString()
                val timeForStopAlertingHours = etIntervalTimeStopHours.text.toString()
                val timeForStopAlertingMinutes = etIntervalTimeStopMinutes.text.toString()
                val settings = Settings(
                    firstTimeToStart =
                        LocalTime.parse("$firstTimeToStartHours:$firstTimeToStartMinutes"),
                    timeForStopAlerting =
                        LocalTime.parse("$timeForStopAlertingHours:$timeForStopAlertingMinutes"),
                    timeForStopAlertingEnabled = cbIntervalTimeStop.isChecked,
                    soundName = actvSound.text.toString()
                )
                viewModel.saveSettings(settings)
            } catch (error: Exception) {
                showMessageAsDialog(INVALID_DATE_FORMAT_ERROR, MessageType.ERROR)
            }
        }
    }

    private fun checkErrors(): Boolean {
        with(binding) {
            val firstTimeToStartHours = etAlertBeforeFirstTimeHours.text.toString()
            val firstTimeToStartMinutes = etAlertBeforeFirstTimeMinutes.text.toString()
            val timeForStopAlertingHours = etIntervalTimeStopHours.text.toString()
            val timeForStopAlertingMinutes = etIntervalTimeStopMinutes.text.toString()
            if (firstTimeToStartHours.isEmpty() ||
                firstTimeToStartMinutes.isEmpty() ||
                timeForStopAlertingHours.isEmpty() ||
                timeForStopAlertingMinutes.isEmpty()) {
                showMessageAsDialog(FILL_FIELDS_ERROR_MSG, MessageType.ERROR)
                return true
            }

            val parsedFirstTimeToStartHours = firstTimeToStartHours.toInt()
            val parsedFirstTimeToStartMinutes = firstTimeToStartMinutes.toInt()
            if ((parsedFirstTimeToStartHours * 60) + parsedFirstTimeToStartMinutes < MIN_TIME_NUMBER) {
                showMessageAsDialog(MIN_TIME_NUMBER_WARNING, MessageType.ERROR)
                return true
            }

            return false
        }
    }

    private fun setupSoundsDropdown() {
        with(binding) {
            val sounds = resources.getStringArray(R.array.alarm_sounds)
            val adapter = ArrayAdapter(requireContext(), R.layout.drowdown_item, sounds)
            actvSound.setAdapter(adapter)
            actvSound.threshold = Int.MAX_VALUE
            actvSound.setOnItemClickListener { _, _, position, _ ->
                val selectedSound = sounds[position]
                playSound(selectedSound)
            }
        }
    }

    private fun playSound(soundName: String) {
        val name = soundName.lowercase().replace(" ", "_")
        val defType = "raw"
        val defPackage = requireContext().packageName
        val soundResourceId = resources.getIdentifier(name, defType, defPackage)
        if (soundResourceId != 0) {
            stopPlaying()
            mediaPlayer = MediaPlayer.create(requireContext(), soundResourceId)
            mediaPlayer?.start()
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    private fun createTextWatcherForTime(maxValue: Int): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editText: Editable?) {
                if (!editText.isNullOrBlank()) {
                    val number = editText.toString().toInt()
                    if (number > maxValue) {
                        showToast(String.format(TIMES_WARNING, maxValue))
                        editText.replace(0, editText.length, maxValue.toString())
                    }
                }
            }
        }
    }

    companion object {
        const val FILL_FIELDS_ERROR_MSG = "Fill in all the fields!"
        const val TIMES_WARNING = "The number should be no more than %s"
        const val MIN_TIME_NUMBER_WARNING =
            "The time before the first launch should not be less than 15 minutes"
        const val INVALID_DATE_FORMAT_ERROR =
            "Incorrect date format. The format for the date: HH:MM"
        const val MAX_HOURS_NUMBER = 23
        const val MAX_MINUTES_NUMBER = 59
        const val MIN_TIME_NUMBER = 15
    }
}