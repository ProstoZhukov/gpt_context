package ru.tensor.sbis.design.selection.ui.model.recipient

import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.design.profile_decl.person.PersonData

/**
 * Модель данных для отображения персоны в компоненте выбора
 *
 * @author ma.kolpakov
 */
interface PersonSelectorItemModel : RecipientSelectorItemModel {

    /**
     * Информация о персоне
     */
    val personData: PersonData

    /**@SelfDocumented */
    val personName: PersonName
}