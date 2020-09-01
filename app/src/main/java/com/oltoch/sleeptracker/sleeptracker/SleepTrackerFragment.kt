package com.oltoch.sleeptracker.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.oltoch.sleeptracker.R
import com.oltoch.sleeptracker.database.SleepDatabase
import com.oltoch.sleeptracker.databinding.FragmentSleepTrackerBinding
import com.oltoch.sleeptracker.sleeptracker.SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        val sleepTrackerViewModel = ViewModelProvider(this, viewModelFactory)
            .get(SleepTrackerViewModel::class.java)

        binding.lifecycleOwner = this
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        val adapter = SleepNightAdapter()
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, {
            it?.let {
                //adapter.data = it //for when diffUtil is not used and we have to get the position manually
                adapter.submitList(it)
            }
        })

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, { night ->
            night?.let {
                this.findNavController()
                    .navigate(actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()
            }
        })

        // Add an Observer on the state variable for showing a SnackBar message
        // when the CLEAR button is pressed.
        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // Reset state to make sure the SnackBar is only shown once, even if the device
                // has a configuration change.
                sleepTrackerViewModel.doneShowingSnackBar()
            }
        })

        return binding.root
    }
}
