package ru.tensor.sbis.communicator.common.data.model

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента для хостинга всех реестров модуля коммуникатор
 *
 * @author da.zhukov
 */
interface CommunicatorHostFragmentFactory : Feature {

    /**
     * Создать фрагмент для хостинга всех реестров модуля коммуникатор
     * @return [Fragment] фрагмент для хостинга всех реестров модуля коммуникатор
     */
    fun createCommunicatorHostFragment(registry: CommunicatorRegistryType, action: DeeplinkAction? = null): Fragment
}