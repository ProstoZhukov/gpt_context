package ru.tensor.sbis.design.recipient_selection.ui.di.singleton

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.design.recipient_selection.contract.RecipientSelectionDependency

/**
 * Инициализатор DI компонента выбора получателей.
 *
 * @param dependency зависимости модуля выбора получателей.
 *
 * @author vv.chekurda
 */
class RecipientSelectionSingletonComponentInitializer(
    private val dependency: RecipientSelectionDependency
) : BaseSingletonComponentInitializer<RecipientSelectionSingletonComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): RecipientSelectionSingletonComponent =
        DaggerRecipientSelectionSingletonComponent.factory()
            .create(
                dependency,
                commonSingletonComponent
            )
}