package ru.tensor.sbis.version_checker.di

import javax.inject.Qualifier
import javax.inject.Scope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class AppName

@Scope
internal annotation class VersioningSingletonScope

@Scope
internal annotation class VersioningFragmentScope

@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class ManagerDispatcher