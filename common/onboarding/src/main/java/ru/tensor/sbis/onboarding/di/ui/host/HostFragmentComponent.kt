package ru.tensor.sbis.onboarding.di.ui.host

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.tensor.sbis.onboarding.di.HostScope
import ru.tensor.sbis.onboarding.di.OnboardingSingletonComponent
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragmentImpl

@HostScope
@Component(dependencies = [OnboardingSingletonComponent::class],
           modules = [
               AndroidInjectionModule::class,
               HostFragmentModule::class])
internal interface HostFragmentComponent : AndroidInjector<OnboardingHostFragmentImpl> {

    override fun inject(instance: OnboardingHostFragmentImpl)
}