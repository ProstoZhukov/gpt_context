package ru.tensor.sbis.toolbox_decl.logging

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для предоставления экрана принудительной отправки логов.
 * TODO: временное решение.
 * После выполнения задачи по темизации окна отправки логов https://online.sbis.ru/opendoc.html?guid=ad4fe3c3-58c4-409c-adb6-06a726474b5b
 * необходимо перейти на использование [LoggingFragmentProvider.getLoggingFragment]
 *
 * @author ai.shlauzer
 */
interface ForceLogDeliveryScreenProvider : Feature {

    /** @SelfDocumented */
    fun getForceLogDeliveryScreen() : Fragment
}
