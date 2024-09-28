package ru.tensor.sbis.design.profile_decl.person

import java.util.*

/**
 * Обработчик нажатий на коллаж, либо одиночное фото
 *
 * @author us.bessonov
 */
interface PersonCollageClickListener {

    /**
     * Вызывается при нажатии на коллаж (если в нём более одного фото)
     */
    fun onCollageClick() = Unit

    /**
     * Вызывается при нажатии на одиночное фото сотрудника, компании, группы, подразделения, а также на коллаж
     * подразделения.
     * UUID здесь nullable, так как он может отсутствовать в отображаемых моделях (например, у контрагентов)
     */
    fun onPersonClick(uuid: UUID?) = Unit
}