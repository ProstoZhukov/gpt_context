package ru.tensor.sbis.communicator_support_channel_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import java.util.UUID

/**
 * Фабрика фабрики (!!!) вью-модели
 * SupportChannelListViewModelFactoryFactory - фабрика, которая будет создана даггером, для передачи в неё параметра mode,
 * который приходит в аргументах фрагмента. Уже параметризованная нужным mode фабрика ViewModelProvider.Factory
 * используется для создания вью-модели
 */
@AssistedFactory
internal interface SupportChannelListViewModelFactoryFactory {
    fun create(
        config: SupportComponentConfig,
        isTablet: Boolean,
        @Assisted("sourceId") sourceId: UUID?,
        @Assisted("conversationId") conversationId: UUID?
    ): SupportChannelListViewModelFactory<SupportChannelListHostViewModel>
}

internal class SupportChannelListViewModelFactory<VIEW_MODEL> @AssistedInject constructor(
    @Assisted private val config: SupportComponentConfig,
    @Assisted private val isTablet: Boolean,
    @Assisted("sourceId") private val sourceId: UUID?,
    @Assisted("conversationId") private val conversationId: UUID?,
    viewModelProvider: SupportChannelListHostVtiewModelFactory,
) : ViewModelProvider.Factory where VIEW_MODEL : ViewModel {
    private val providers = mapOf<Class<*>, SupportChannelListHostVtiewModelFactory>(
        SupportChannelListHostViewModel::class.java to viewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.create(config, isTablet, sourceId, conversationId) as T
    }
}