package com.example.alertofevents.ui.settings

import android.media.MediaPlayer
import android.os.Bundle
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.settingsFragmentTitle))
        setDisplayHomeAsUpEnabled(false)
        setupSoundsDropdown()
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

    private fun render(settings: Settings) {
        with(binding) {
            etAlertBeforeFirstTimeHours.setText(settings.firstTimeToStart.getHoursHH())
            etAlertBeforeFirstTimeMinutes.setText(settings.firstTimeToStart.getMinutesMM())
            etIntervalTimeHours.setText(settings.beforeOnsetTime.getHoursHH())
            etIntervalTimeMinutes.setText(settings.beforeOnsetTime.getMinutesMM())
            etIntervalTimeStopHours.setText(settings.timeForStopAlerting.getHoursHH())
            etIntervalTimeStopMinutes.setText(settings.timeForStopAlerting.getMinutesMM())
            actvSound.setText(settings.soundName, false)
            btnSaveSettings.setOnClickListener {
                onClickSaveButton()
            }
        }
    }

    private fun onClickSaveButton() {
        with(binding) {
            try {
                val firstTimeToStartHours = etAlertBeforeFirstTimeHours.text.toString()
                val firstTimeToStartMinutes = etAlertBeforeFirstTimeMinutes.text.toString()
                val beforeOnsetTimeHours = etIntervalTimeHours.text.toString()
                val beforeOnsetTimeMinutes = etIntervalTimeMinutes.text.toString()
                val timeForStopAlertingHours = etIntervalTimeStopHours.text.toString()
                val timeForStopAlertingMinutes = etIntervalTimeStopMinutes.text.toString()
                val settings = Settings(
                    firstTimeToStart = LocalTime.parse("$firstTimeToStartHours:$firstTimeToStartMinutes"),
                    beforeOnsetTime = LocalTime.parse("$beforeOnsetTimeHours:$beforeOnsetTimeMinutes"),
                    timeForStopAlerting = LocalTime.parse("$timeForStopAlertingHours:$timeForStopAlertingMinutes"),
                    soundName = actvSound.text.toString())
                viewModel.saveSettings(settings)
            } catch (error: Exception) {
                showMessageAsDialog(INVALID_DATE_FORMAT_ERROR, MessageType.ERROR)
            }
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

    companion object {
        const val INVALID_DATE_FORMAT_ERROR = "Incorrect date format"
    }
}