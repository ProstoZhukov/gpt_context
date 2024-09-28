package ru.tensor.sbis.version_checker.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.version_checker.domain.debug.VersioningDebugTool
import javax.inject.Inject

/**
 * Фабрика [SettingsVersionUpdateDebugViewModelImpl]
 *
 * @author us.bessonov
 */
internal class SettingsVersionUpdateDebugVmFactory @Inject constructor(
    private val versioningDebugTool: VersioningDebugTool
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        require(modelClass == SettingsVersionUpdateDebugViewModelImpl::class.java) {
            "Unsupported ViewModel type $modelClass"
        }
        return SettingsVersionUpdateDebugViewModelImpl(versioningDebugTool) as VM
    }
}