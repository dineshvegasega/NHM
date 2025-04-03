package com.nhm.distribution.screens.main.distribution

import androidx.lifecycle.ViewModel
import com.nhm.distribution.networking.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DistributionViewModel @Inject constructor(private val repository: Repository): ViewModel() {

}