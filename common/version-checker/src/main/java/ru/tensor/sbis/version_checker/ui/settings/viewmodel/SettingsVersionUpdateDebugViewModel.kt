package ru.tensor.sbis.version_checker.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

/**
 * Интерфейс вьюмодели экрана отладки обновлений
 *
 * @author us.bessonov
 */
internal interface SettingsVersionUpdateDebugViewModel {

    /**
     * Выбранный тип обновления
     */
    val selectedStatus: LiveData<UpdateStatus>

    /**
     * Номер версии в поле ввода
     */
    val version: String

    /** @SelfDocumented */
    fun setSelectedUpdateStatus(status: UpdateStatus)

    /** @SelfDocumented */
    fun onVersionChanged(newVersion: String)
}