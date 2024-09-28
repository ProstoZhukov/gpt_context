package ru.tensor.sbis.onboarding.di.ui.page

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.onboarding.di.FeatureScope
import ru.tensor.sbis.onboarding.ui.page.OnboardingFeatureFragment
import javax.inject.Named

@Suppress("unused")
@Module
internal abstract class FeatureFragmentModule {

    @Suppress("unused")
    companion object {

        @FeatureScope
        @Provides
        @Named(ARG_FEATURE_UUID)
        fun provideFeatureUuid(fragment: OnboardingFeatureFragment) = fragment.params.uuid
    }
}

internal const val ARG_FEATURE_UUID = "featureUuid"
