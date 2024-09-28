package ru.tensor.sbis.toolbox_decl.apptools

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет статус запуска приложения из автотестов.
 *
 * @author us.bessonov
 */
interface AutotestsLaunchStatusProvider : Feature {

    /** @SelfDocumented */
    val isAutotestsLaunch: Boolean
}