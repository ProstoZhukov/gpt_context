package ru.tensor.sbis.recipient_selection.profile.di

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionDependency

/**
 * Инициализатор компонента выбора получателей
 * @param dependency Зависимости модуля
 */
class RecipientSelectionSingletonComponentInitializer(
    private val dependency: RecipientSelectionDependency
) : BaseSingletonComponentInitializer<RecipientSelectionSingletonComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): RecipientSelectionSingletonComponent =
        DaggerRecipientSelectionSingletonComponent.factory().create(dependency, commonSingletonComponent)
}