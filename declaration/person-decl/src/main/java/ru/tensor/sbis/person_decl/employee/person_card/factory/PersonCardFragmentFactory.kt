package ru.tensor.sbis.person_decl.employee.person_card.factory

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.PersonCardConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.data.PersonCardArgs
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика фрагмента карточки сотрудника
 *
 * @author ra.temnikov
 */
interface PersonCardFragmentFactory : Feature {

    /**
     * Получить текущую конфигурацию для последующего изменения под свои нужды
     * @return текущая конфигурация приложения (см. [PersonCardConfiguration])
     */
    fun takeCurrentConfiguration(): PersonCardConfiguration

    /**
     * Создать экземпляр фрагмента карточки сотрудника
     *
     * @param personUuid идентификатор персоны
     * @param configuration нештатная конфигурация карточки
     * @return новый экземпляр фрагмента
     */
    fun createPersonCardFragment(personUuid: UUID, configuration: PersonCardConfiguration? = null): Fragment

    /**
     * Создать экземпляр фрагмента карточки сотрудника с преднастройками для специфичных сценариев
     *
     * @param personCardArgs модель аргументов для открытия карточки
     * @param configuration нештатная конфигурация карточки
     * @return новый экземпляр фрагмента
     */
    fun createPersonCardFragment(
        personCardArgs: PersonCardArgs,
        configuration: PersonCardConfiguration? = null
    ): Fragment

    /**
     * Создать экземпляр фрагмента карточки сотрудника по содержимому intent активности
     *
     * @param intentExtras данные intent`a актиновности карточки сотрудника
     * @return новый экземпляр фрагмента
     */
    fun createPersonCardFragment(intentExtras: Bundle): Fragment
}