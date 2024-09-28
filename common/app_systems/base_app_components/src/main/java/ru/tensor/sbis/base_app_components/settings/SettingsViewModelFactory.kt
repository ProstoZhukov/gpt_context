package ru.tensor.sbis.base_app_components.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @SelfDocumented
 *
 * @author ma.kolpakov
 */
class SettingsViewModelFactory(private val settingItemsProvider: SettingItemsProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL =
        SettingsViewModel(
            settingItemsProvider
        ) as VIEW_MODEL
}