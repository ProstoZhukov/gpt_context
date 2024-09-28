package ru.tensor.sbis.person_decl.status.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик [Fragment] на изменение статуса
 */
interface StatusChooserFragmentProvider : Feature {

    /**
     * Позволяет получить [Fragment] изменения статуса
     */
    fun createStatusChooserFragment(withNavigation: Boolean = true): Fragment
}