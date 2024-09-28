package ru.tensor.sbis.toolbox_decl.apptools

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик параметров запуска приложения в режиме автотестов.
 *
 * @see `ru.tensor.sbis.toolbox_decl.apptools.IntentAction.Extra`.
 *
 * @author us.bessonov
 */
interface AutotestsParametersProvider : Feature {

    /**
     * Должно ли срабатывать первоначальное открытие Аккордеона при запуске под автотестами.
     */
    val showAccordionOnAutotestsLaunch: Boolean

    /**
     * Восстанавливать ли ранее открытый раздел навигации при перезапуске.
     */
    val restoreActiveNavigationItem: Boolean
}