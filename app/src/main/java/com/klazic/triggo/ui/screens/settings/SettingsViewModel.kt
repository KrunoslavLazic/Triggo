package com.klazic.triggo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klazic.triggo.data.prefs.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val nameInput: String = "",
    val savedName: String = "",
    val theme: String = "system",
    val dynamicColors: Boolean = false,
    val avatarIndex: Int = 0,
    val isSavingName: Boolean = false
)

class SettingsViewModel(
    private val prefs: UserPrefs
):ViewModel(){

    private val _nameInput = MutableStateFlow("")

    val state: StateFlow<SettingsUiState> = combine(
        prefs.name,
        prefs.theme,
        prefs.dynamic,
        prefs.avatar,
        _nameInput
    ) { savedName, theme, dynamic, avatar, nameInput ->
        SettingsUiState(
            savedName = savedName,
            theme = theme,
            dynamicColors = dynamic,
            avatarIndex = avatar,
            nameInput = nameInput
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onNameChange(newName: String){
        _nameInput.value = newName
    }

    fun saveName(){
        val n = state.value.nameInput.trim()
        viewModelScope.launch {
            setSaving(true)
            prefs.setName(n)
            _nameInput.value = ""
            setSaving(false)
        }
    }

    fun selectTheme(theme: String){
        viewModelScope.launch {
            prefs.setTheme(theme)
        }
    }

    fun setDynamicColors(enabled: Boolean){
        viewModelScope.launch {
            prefs.setDynamic(enabled)
        }
    }

    fun setAvatar(index: Int) {
        viewModelScope.launch {
            prefs.setAvatar(index)
        }
    }

    private fun setSaving(s: Boolean){
        _saving.update { s }
    }
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    class SettingsViewModelFactory(
        private val prefs: UserPrefs
    ): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(prefs) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}