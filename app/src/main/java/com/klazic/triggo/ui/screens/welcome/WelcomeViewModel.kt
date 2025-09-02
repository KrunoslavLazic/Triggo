package com.klazic.triggo.ui.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klazic.triggo.data.prefs.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WelcomeUiState(
    val name: String = "",
    val error: String? = null,
    val isSaving: Boolean = false
)

class WelcomeViewModel(private val prefs: UserPrefs) : ViewModel() {
    private val _state = MutableStateFlow(WelcomeUiState())
    val state: StateFlow<WelcomeUiState> = _state

    fun onNameChange(s: String) {
        _state.update { it.copy(name = s, error = null) }
    }

    fun saveAndContinue(onSuccess: () -> Unit) {
        val n = _state.value.name.trim()

        if (n.isEmpty()) {
            _state.update { it.copy(error = "Please enter your name") }
            return
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            prefs.setName(n)
            _state.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}

class WelcomeViewModelFactory(
    private val prefs:UserPrefs
): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return WelcomeViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}