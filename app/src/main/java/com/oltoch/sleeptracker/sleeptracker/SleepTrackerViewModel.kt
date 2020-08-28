package com.oltoch.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.oltoch.sleeptracker.database.SleepDatabaseDao

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {
}

