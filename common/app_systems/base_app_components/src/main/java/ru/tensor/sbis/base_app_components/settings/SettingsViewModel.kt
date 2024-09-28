package ru.tensor.sbis.base_app_components.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.settings_screen.content.ContentHolder

/**
 * Модель представления экрана списка настроек.
 * При старте заполняет список из [settingItemsProvider].
 *
 * @author ma.kolpakov
 */
class SettingsViewModel(
    private val settingItemsProvider: SettingItemsProvider
) : ViewModel() {

    private val _content = MutableStateFlow(ContentHolder())
    /** @SelfDocumented */
    val content: StateFlow<ContentHolder> = _content

    private val _progress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    /** @SelfDocumented */
    val progress: StateFlow<Boolean> = _progress

    private val _title: MutableStateFlow<String?> = MutableStateFlow(StringUtils.EMPTY)
    /** @SelfDocumented */
    val title: StateFlow<String?> = _title

    init {
        updateSettingsItems()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun updateSettingsItems() {
        viewModelScope.launch {
            _progress.emit(true)
            settingItemsProvider.prepare()
            val settingsData = settingItemsProvider.provide()
            _content.emit(settingsData.items)
            _title.emit(settingsData.title)
            _progress.emit(false)
        }
    }

}