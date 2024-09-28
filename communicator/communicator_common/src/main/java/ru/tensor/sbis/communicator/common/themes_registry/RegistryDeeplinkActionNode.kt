package ru.tensor.sbis.communicator.common.themes_registry

import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode

/**
 * Реестр поддерживающий диплинки.
 * Служит для проверки необходимости делегировать обработку диплинков текущему реестру.
 *
 * @author vv.chekurda
 */
interface RegistryDeeplinkActionNode : DeeplinkActionNode {

    /**
     * Проверить является ли текущий [deeplink] внутренним действимем реестра.
     */
    fun isRegistryDeeplinkAction(deeplink: DeeplinkAction): Boolean
}