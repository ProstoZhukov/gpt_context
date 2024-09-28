package ru.tensor.sbis.person_decl.employee.person_card.factory

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.PersonCardConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.data.PersonCardArgs
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания [Intent] активности карточки сотрудника
 *
 * @author ra.temnikov
 */
interface PersonCardIntentFactory : Feature {

    /**
     * Создать [Intent] активности карточки сотрудника
     *
     * @param context    контекст
     * @param personUuid идентификатор персоны
     */
    fun createPersonCardIntent(context: Context, personUuid: UUID): Intent

    /**
     * Создать [Intent] активности карточки сотрудника
     *
     * @param context    контекст
     * @param personUuid идентификатор персоны
     * @param configuration нештатная конфигурация карточки
     */
    fun createPersonCardIntent(
        context: Context,
        personUuid: UUID,
        configuration: PersonCardConfiguration? = null
    ): Intent

    /**
     * Создать [Intent] активности карточки сотрудника с преднастройками для специфичных сценариев
     *
     * @param context        контекст
     * @param personCardArgs модель аргументов для открытия карточки
     * @param configuration нештатная конфигурация карточки
     */
    fun createPersonCardIntent(
        context: Context,
        personCardArgs: PersonCardArgs,
        configuration: PersonCardConfiguration? = null
    ): Intent
}