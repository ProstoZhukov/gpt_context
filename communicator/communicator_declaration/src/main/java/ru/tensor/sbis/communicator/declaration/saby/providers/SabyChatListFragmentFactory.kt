package ru.tensor.sbis.communicator.declaration.saby.providers

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента реестра чатов Saby get.
 *
 * @author vv.chekurda
 */
@Suppress("unused")
interface SabyChatListFragmentFactory : Feature {

    /**
     * Создать фрагмент реестра чатов Saby get.
     * @return [Fragment] фрагмент реестра чатов.
     */
    fun createSabyChatListFragment(): Fragment
}