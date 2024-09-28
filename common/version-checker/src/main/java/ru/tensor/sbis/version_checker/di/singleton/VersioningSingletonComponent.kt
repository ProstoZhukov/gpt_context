package ru.tensor.sbis.version_checker.di.singleton

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.di.VersioningSingletonScope
import ru.tensor.sbis.version_checker.di.subcomponents.DebugUpdateFragmentComponent
import ru.tensor.sbis.version_checker.di.subcomponents.RecommendedUpdateFragmentComponent
import ru.tensor.sbis.version_checker.di.subcomponents.RequiredUpdateFragmentComponent
import ru.tensor.sbis.version_checker.domain.InstallerManager
import ru.tensor.sbis.version_checker.domain.VersionManager
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory

/**
 * DI комопнент модуля версионирования
 *
 * @author as.chadov
 */
@VersioningSingletonScope
@Component(
    modules = [VersioningSingletonModule::class]
)
internal interface VersioningSingletonComponent {

    val versionManager: VersionManager

    val installerManager: InstallerManager

    val commandFactory: UpdateCommandFactory

    val versionDependency: VersioningDependency

    val analytics: Analytics

    fun recommendedComponentFactory(): RecommendedUpdateFragmentComponent

    fun requiredComponentFactory(): RequiredUpdateFragmentComponent

    fun debugComponentFactory(): DebugUpdateFragmentComponent

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance dependency: VersioningDependency
        ): VersioningSingletonComponent
    }
}
