package ru.tensor.sbis.version_checker.ui.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.version_checker.domain.debug.VersioningDebugTool
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

/**
 * Реализация [SettingsVersionUpdateDebugViewModel]
 *
 * @author us.bessonov
 */
internal class SettingsVersionUpdateDebugViewModelImpl(
    private val versioningDebugTool: VersioningDebugTool
) : ViewModel(), SettingsVersionUpdateDebugViewModel {

    override val selectedStatus = MutableLiveData(versioningDebugTool.debugStatus)
    override val version = versioningDebugTool.debugVersion
    private var futureDebugVersion = version

    override fun setSelectedUpdateStatus(status: UpdateStatus) {
        if (selectedStatus.value != status) {
            selectedStatus.value = status
        }
    }

    override fun onVersionChanged(newVersion: String) {
        if (futureDebugVersion != newVersion) {
            futureDebugVersion = newVersion
        }
    }

    override fun onCleared() {
        applyDebugSettings()
        super.onCleared()
    }

    private fun applyDebugSettings() {
        if (futureDebugVersion.isNotBlank() && futureDebugVersion != versioningDebugTool.realVersion) {
            selectedStatus.value?.let(versioningDebugTool::applyUpdateStatus)
            versioningDebugTool.applyDebugVersion(futureDebugVersion)
        }
    }
}