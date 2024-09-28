package ru.tensor.sbis.person_decl.employee.person_card.data

import android.os.Bundle
import java.util.UUID

/**
 * Модель параметров для открытия карточки сотрудника со специфичным поведением
 *
 * @param personUuid                  идентификатор персоны
 * @param isOpenedFromCalendarScreen  true, если открытие карточки из календаря
 * @param isOpenedFromActivityScreen  true, если открытие карточки из экрана активности календаря
 * @param isOpenedFromMyProfileScreen true, если открытие карточки (для [PersonCardTabType] != PERSON_CARD) из экрана моего профиля
 * @param extraBundle                 bundle для проброса опций открытия экранов календаря
 *
 * @author ra.temnikov
 */
data class PersonCardArgs(
    val personUuid: UUID,
    val isOpenedFromCalendarScreen: Boolean = false,
    val isOpenedFromActivityScreen: Boolean = false,
    val isOpenedFromMyProfileScreen: Boolean = false,
    val extraBundle: Bundle? = null
)