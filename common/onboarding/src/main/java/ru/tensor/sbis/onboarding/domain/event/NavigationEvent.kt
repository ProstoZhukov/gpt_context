package ru.tensor.sbis.onboarding.domain.event

import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.fragment.app.Fragment

/**
 * Событие навигации на экране приветсвия
 */
internal interface NavigateEvent

/**
 * Событие навигации к следующему экрану фрагмент-фичи
 */
internal class NavigateForwardEvent : NavigateEvent

/**
 * Событие навигации к предыдущему экрану фрагмент-фичи
 */
internal class NavigateBackwardEvent : NavigateEvent

/**
 * Событие открытия пользовательского экрана
 *
 * @param creator объект создатель фрагмента
 * @param containerId идентификатор контейнера для создаваемого фрагмента
 */
internal class OpenCustomScreen(
    var creator: (() -> Fragment),
    var screenKey: String = "",
    @IdRes var containerId: Int = ID_NULL
) : NavigateEvent

/**
 * Событие скрытия пользовательского экрана
 */
internal class DismissCustomScreen(
    var screenKey: String = ""
) : NavigateEvent

