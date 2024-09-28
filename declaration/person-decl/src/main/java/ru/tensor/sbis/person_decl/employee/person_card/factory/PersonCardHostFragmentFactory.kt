package ru.tensor.sbis.person_decl.employee.person_card.factory

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.PersonCardConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.data.PersonCardArgs
import ru.tensor.sbis.person_decl.employee.person_card.data.PersonCardTabType
import ru.tensor.sbis.person_decl.employee.person_card.data.PersonCardTabType.PersonCard
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Фабрика хост-фрагмента карточки сотрудника для планшетного отображения со вкладками
 * или для открытия конкретных экранов внутренней навигации
 *
 * @author ra.temnikov
 */
interface PersonCardHostFragmentFactory : Feature {

    /**
     * Создать экземпляр хост-фрагмента карточки сотрудника
     *
     * @param personUuid идентификатор персоны
     * @param tabType тип вкладки для открытия конкретного раздела карточки
     * @param configuration нештатная конфигурация карточки
     * @return новый экземпляр фрагмента
     */
    fun createPersonCardHostFragment(
        personUuid: UUID,
        tabType: PersonCardTabType = PersonCard,
        configuration: PersonCardConfiguration? = null
    ): Fragment

    /**
     * Создать экземпляр хост-фрагмента карточки сотрудника с преднастройками для специфичных сценариев
     *
     * @param personCardArgs модель аргументов для открытия карточки
     * @param tabType тип вкладки для открытия конкретного раздела карточки
     * @param configuration нештатная конфигурация карточки
     * @return новый экземпляр фрагмента
     */
    fun createPersonCardHostFragment(
        personCardArgs: PersonCardArgs,
        tabType: PersonCardTabType = PersonCard,
        configuration: PersonCardConfiguration? = null
    ): Fragment

    /**
     * Создать экземпляр хост-фрагмента карточки сотрудника по содержимому intent активности
     *
     * @param intentExtras данные intent`a актиновности карточки сотрудника
     * @param configuration нештатная конфигурация карточки
     * @return новый экземпляр фрагмента
     */
    fun createPersonCardHostFragment(intentExtras: Bundle): Fragment
}