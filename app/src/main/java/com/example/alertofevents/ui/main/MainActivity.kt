package com.example.alertofevents.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.alertofevents.R
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                MainViewModel.Factory> { factory ->
                factory.create()
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_calendar_events,
                R.id.navigation_event,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setNavigationController(navController)
        installSplashScreen()
        subscribeToViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.firstLoad()
    }

    private fun setNavigationController(navController: NavController) {
        binding.navView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            navController.popBackStack(item.itemId, inclusive = false)
            true
        }
    }

    private fun subscribeToViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.viewState.collect(::onViewState) }
            }
        }
    }

    private fun onViewState(state: LoadableData<Boolean>) {
        when(state) {
            is LoadableData.Success -> {
                val isWorkerScheduled = state.value
                if (!isWorkerScheduled) {
                    val periodicWork: PeriodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                        NotificationWorker.EXECUTION_FREQUENCY_MINUTES,
                        TimeUnit.MINUTES
                    ).build()
                    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                        "myAlarm",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        periodicWork)
                    viewModel.setIsWorkerScheduled(true)
                }
            }
            is LoadableData.Loading -> {
                // ignored
            }
            is LoadableData.Error -> {
                // ignored
            }
        }
    }
}