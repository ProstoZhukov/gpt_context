@file:Suppress("unused")

package ru.tensor.sbis.version_checker.di.singleton

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.tensor.sbis.version_checker.di.DefaultDispatcher
import ru.tensor.sbis.version_checker.di.IoDispatcher
import ru.tensor.sbis.version_checker.di.ManagerDispatcher

@Module
object VersioningDispatcherModule {
    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @ManagerDispatcher
    @Provides
    fun providesDispatchers() = Dispatchers.Default
}