package ru.tensor.sbis.communicator.common.import_contacts

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента импорта контактов
 *
 * @author da.zhukov
 */
interface ImportContactsConfirmationFragmentFactory : Feature {

    /**
     * Создать фрагмент импорта контактов
     * @return [Fragment] фрагмент импорта контактов
     */
    fun createImportContactsConfirmationFragment(): Fragment
}