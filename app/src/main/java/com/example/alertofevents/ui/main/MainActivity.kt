package com.example.alertofevents.ui.main

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.example.alertofevents.R
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.databinding.ActivityMainBinding
import com.example.alertofevents.domain.model.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import java.time.LocalTime
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
        requestPermissions()
    }

    override fun onStart() {
        super.onStart()
        viewModel.firstLoad()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
            ),
            1
        )
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

    private fun onViewState(state: LoadableData<Settings>) {
        when(state) {
            is LoadableData.Success -> {
                val settings: Settings = state.value
                val firstTimeToStart: LocalTime = settings.firstTimeToStart
                val minutes = (firstTimeToStart.hour * 60) + firstTimeToStart.minute
                NotificationWorker.scheduleWork(
                    applicationContext,
                    minutes.toLong(),
                    TimeUnit.MINUTES
                )
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