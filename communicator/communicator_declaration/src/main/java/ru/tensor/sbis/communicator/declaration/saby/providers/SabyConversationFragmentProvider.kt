package ru.tensor.sbis.communicator.declaration.saby.providers

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.saby.model.SabyChatParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента чата Saby get.
 *
 * @author vv.chekurda
 */
@Suppress("unused")
interface SabyConversationFragmentFactory : Feature {

    /**
     * Создать фрагмент чата.
     * @param params параметры для создания фрагмента чата.
     */
    fun createSabyConversationFragment(params: SabyChatParams): Fragment
}