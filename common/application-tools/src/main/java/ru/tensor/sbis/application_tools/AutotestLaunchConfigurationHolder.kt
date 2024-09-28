package ru.tensor.sbis.application_tools

import ru.tensor.sbis.toolbox_decl.apptools.AutotestsLaunchStatusProvider
import ru.tensor.sbis.toolbox_decl.apptools.AutotestsParametersProvider

/**
 * Предназначен для предоставления конфигурации запуска приложения из автотестов через плагинную систему.
 *
 * @author us.bessonov
 */
internal object AutotestLaunchConfigurationHolder : AutotestsLaunchStatusProvider, AutotestsParametersProvider {

    override var isAutotestsLaunch = false

    override var showAccordionOnAutotestsLaunch = false

    override var restoreActiveNavigationItem = true
}