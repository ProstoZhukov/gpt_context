package ru.tensor.sbis.onboarding.di.ui.host

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.onboarding.di.FeatureScope
import ru.tensor.sbis.onboarding.di.HostScope
import ru.tensor.sbis.onboarding.di.ui.page.FeatureFragmentModule
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.domain.interactor.HostInteractor
import ru.tensor.sbis.onboarding.domain.util.PermissionHelper
import ru.tensor.sbis.onboarding.ui.base.OnboardingNextPage
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragmentImpl
import ru.tensor.sbis.onboarding.ui.host.adapter.FeaturePageCreator
import ru.tensor.sbis.onboarding.ui.host.adapter.PageListHolder
import ru.tensor.sbis.onboarding.ui.page.OnboardingFeatureFragment
import ru.tensor.sbis.onboarding.ui.view.SwipeDelegate
import javax.inject.Named

@Suppress("unused")
@Module(
    includes = [
        AndroidInjectionModule::class,
        HostFragmentModule.FragmentBinder::class,
        HostFragmentModule.FragmentInjector::class]
)
internal class HostFragmentModule(private val fragment: OnboardingHostFragmentImpl) {

    @HostScope
    @Provides
    fun provideFragment(): Fragment = fragment

    @HostScope
    @Provides
    fun provideOnboardingNextPage(): OnboardingNextPage = fragment

    @HostScope
    @Provides
    @Named("isDialogContent")
    fun provideIsDialogContent() = fragment.isDialogContent

    @HostScope
    @Provides
    fun providePermissionInteractor(
        repository: OnboardingRepository
    ) = PermissionHelper(
        repository = repository,
        permissionDelegate = fragment
    )

    @HostScope
    @Provides
    fun provideSwipeDelegate(
        holder: PageListHolder,
        permissionInteractor: PermissionHelper
    ) = SwipeDelegate(
        fragment = fragment,
        holder = holder,
        permissionInteractor = permissionInteractor
    )

    @HostScope
    @Named(ARG_PAGE_COUNTER)
    @Provides
    fun providePageCounterChannel(): Subject<Int> = PublishSubject.create()

    @Module
    @Suppress("unused")
    interface FragmentBinder {

        @HostScope
        @Binds
        fun provideOnboardingPageListHolder(impl: HostInteractor): PageListHolder

        @HostScope
        @Binds
        fun provideFeaturePageCreator(impl: HostInteractor): FeaturePageCreator
    }

    @Module
    interface FragmentInjector {

        @FeatureScope
        @ContributesAndroidInjector(modules = [FeatureFragmentModule::class])
        fun onboardingFeatureFragmentInjector(): OnboardingFeatureFragment
    }
}

/** счетчик страниц */
internal const val ARG_PAGE_COUNTER = "pageCounter"