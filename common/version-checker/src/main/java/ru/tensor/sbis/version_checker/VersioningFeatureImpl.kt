package ru.tensor.sbis.version_checker

import android.content.Intent
import ru.tensor.sbis.version_checker.domain.dispatching.VersioningDispatcherImpl
import ru.tensor.sbis.version_checker.domain.utils.VersioningDemoOpenerImpl
import ru.tensor.sbis.version_checker_decl.SbisApplicationManager
import ru.tensor.sbis.version_checker_decl.VersioningDebugActivator
import ru.tensor.sbis.version_checker_decl.VersioningDemoOpener
import ru.tensor.sbis.version_checker_decl.VersioningDispatcher
import ru.tensor.sbis.version_checker_decl.VersioningFeature
import ru.tensor.sbis.version_checker_decl.VersioningInitializer

/**
 * Реализация [VersioningFeature]
 *
 * @author as.chadov
 */
internal class VersioningFeatureImpl : VersioningFeature {

    private val versioningComponent get() = VersionCheckerPlugin.versioningComponent

    private val versionManager by lazy { versioningComponent.versionManager }

    override val versioningDispatcher: VersioningDispatcher by lazy { VersioningDispatcherImpl() }

    override val versioningInitializer: VersioningInitializer = versionManager

    override val versioningDebugActivator: VersioningDebugActivator = versionManager

    override val sbisApplicationManager: SbisApplicationManager = versioningComponent.installerManager

    override val versioningDemoOpener: VersioningDemoOpener by lazy { VersioningDemoOpenerImpl() }

    override fun getForcedUpdateAppActivityIntent(ifObsolete: Boolean): Intent? =
        versionManager.getForcedUpdateAppActivityIntent(ifObsolete)

    override fun isApplicationCriticalIncompatibility(): Boolean =
        versionManager.isApplicationCriticalIncompatibility()

    override fun isActualVersion(): Boolean = versionManager.isActualVersion()
}