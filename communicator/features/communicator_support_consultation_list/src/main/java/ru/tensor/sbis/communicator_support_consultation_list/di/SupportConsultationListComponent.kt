package ru.tensor.sbis.communicator_support_consultation_list.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFragmentFactory
import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFragmentFactoryImpl
import ru.tensor.sbis.communicator_support_consultation_list.mapper.SupportConsultationMapperFactory
import ru.tensor.sbis.communicator_support_consultation_list.presentation.SupportConsultationListFragment
import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
internal annotation class PluginScope

/**
 * Компонент для фрагмента реестра обращений в техподдержку
 */
@PluginScope
@Component(modules = [FeatureModule::class])
internal interface SupportRequestsListComponent {
    fun getFeature(): SupportConsultationListFragmentFactory

    fun inject(fragment: SupportConsultationListFragment)

    val supportCConsultationMapperFactory: SupportConsultationMapperFactory
}

/**
 * Модуль для предоставления фичи
 */
@Module
internal class FeatureModule {

    @Provides
    @PluginScope
    fun provideFeature(): SupportConsultationListFragmentFactory =
        SupportConsultationListFragmentFactoryImpl()
}