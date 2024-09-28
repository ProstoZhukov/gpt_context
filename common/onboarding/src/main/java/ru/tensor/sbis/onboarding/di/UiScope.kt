package ru.tensor.sbis.onboarding.di

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class HostScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class FeatureScope