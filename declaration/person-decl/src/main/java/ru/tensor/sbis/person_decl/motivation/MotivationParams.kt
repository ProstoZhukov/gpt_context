package ru.tensor.sbis.person_decl.motivation

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import java.util.*

/**
 * Ссылки на необходимые классы для возможности
 * размещения внутренней навигации на внешнем контейнере
 *
 * @property uuid - [UUID] пользователя для которого отображаются данные.
 * @property lifecycle - [Lifecycle] на который можно навесить слушателя для безопасной навигации.
 * @property firstContainerId - id контейнера для первого экрана
 * @property hostContainerId - id контейнера хоста, для отображения экранов кроме первого
 * @property fragmentManager - необходимо передавать [Fragment.childFragmentManager]
 *
 * @author ra.temnikov
 */
data class MotivationParams(
    val uuid: UUID,
    val lifecycle: Lifecycle,
    val firstContainerId: Int,
    val hostContainerId: Int,
    val fragment: Fragment
)