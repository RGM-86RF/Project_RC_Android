package com.antoniogage.projectrc

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val dataStore: ThemeDataStore) : ViewModel() {


    val theme: StateFlow<String> = dataStore.getTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "System default"
    )


    fun saveTheme(theme: String) {
        viewModelScope.launch {
            dataStore.saveTheme(theme)
        }
    }
}