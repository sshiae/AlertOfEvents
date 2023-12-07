package com.example.alertofevents.ui.settings

import androidx.lifecycle.viewModelScope
import com.example.alertofevents.base.ui.BaseViewModel
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.common.emitError
import com.example.alertofevents.common.emitLoading
import com.example.alertofevents.common.emitSuccess
import com.example.alertofevents.domain.interactor.AlertOfEventsInteractor
import com.example.alertofevents.domain.model.Settings
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for working with settings
 */
@HiltViewModel(assistedFactory = SettingsViewModel.Factory::class)
class SettingsViewModel @AssistedInject constructor(
    private val interactor: AlertOfEventsInteractor
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): SettingsViewModel
    }

    /**
     * [Flow] to get the settings
     */
    private val settingsStateFlow: MutableStateFlow<LoadableData<Settings>> =
        MutableStateFlow(LoadableData.Loading())
    val settingsState = settingsStateFlow.asStateFlow()

    override fun firstLoad() {
        viewModelScope.launch {
            try {
                settingsStateFlow.emitLoading()
                val settings = interactor.getSettings()
                settingsStateFlow.emitSuccess(settings)
            } catch (error: Exception) {
                settingsStateFlow.emitError(error)
            }
        }
    }

    /**
     * Used for save settings
     */
    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            try {
                showLoading()
                interactor.saveSettings(settings)
                showToast(SUCCESSFUL_SAVE_MSG)
            } catch (error: Exception) {
                showErrorDialog(error.message)
            } finally {
                hideLoading()
            }
        }
    }

    companion object {
        private const val SUCCESSFUL_SAVE_MSG = "The settings have been saved successfully"
    }
}