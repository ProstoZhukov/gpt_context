package ru.tensor.sbis.onboarding.domain.interactor.usecase

import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL

/**
 * Модель описывающая основные атрибуты состояния хоста
 *
 * @param isPreventBackSwipe true если запрещен свайп назад по экранам фич
 * @param isBackPressed rue если осуществляется свайп назад по нажатию на кнопку "Назад"
 * @param isAutoSwitchable true если страницы приветственного экрана будут перелистываться автоматически
 * @param longestDescriptionText длиннейшие описание из всех страниц приветственного экрана
 *
 * @author as.chadov
 */
internal data class HostState(
    val isPreventBackSwipe: Boolean = false,
    val isBackPressed: Boolean = false,
    val isAutoSwitchable: Boolean = false,
    val longestDescriptionText: String = "",
    val firstImageResId: Int = ID_NULL
) {
    companion object {
        val EMPTY = HostState()
    }
}