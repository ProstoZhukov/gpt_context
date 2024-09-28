package ru.tensor.sbis.communicator.declaration.communicator_support_channel_list

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика хост фрагмента реестра чатов сабигет.
 *
 * @author da.zhukov
 */
interface SabyGetChannelListFragmentFactory : Feature {

    /** SelfDocumented */
    fun createSabyGetChannelListHostFragment(
        showLeftPanelOnToolbar: Boolean = true,
        isBrand: Boolean = false,
        salePoin: UUID? = null
    ): Fragment
}
