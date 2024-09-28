@file:Suppress("unused")

package ru.tensor.sbis.version_checker.di.singleton

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.di.AppName
import ru.tensor.sbis.version_checker.di.VersioningSingletonScope
import ru.tensor.sbis.version_checker.domain.VersionManager
import ru.tensor.sbis.version_checker.domain.debug.VersioningDebugTool

@Module(includes = [VersioningSingletonModule.BindsDIModule::class, VersioningDispatcherModule::class])
internal class VersioningSingletonModule {

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)

    @VersioningSingletonScope
    @Provides
    fun provideApiService(dependency: VersioningDependency) = dependency.apiService()

    @VersioningSingletonScope
    @Provides
    fun provideNetworkUtils(dependency: VersioningDependency) = dependency.networkUtils

    @AppName
    @Provides
    fun provideAppName(dependency: VersioningDependency): String =
        dependency.getVersioningSettings().appName

    @Module
    interface BindsDIModule {

        @Binds
        fun provideVersioningDebugTool(impl: VersionManager): VersioningDebugTool
    }
}