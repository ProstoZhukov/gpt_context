package ru.tensor.sbis.onboarding.domain.interactor.usecase

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL

/**
 * Модель описывающая основные атрибуты состояния страницы фичи
 *
 * @author as.chadov
 *
 * @param isFeature true если страница предоставляет описание фичи
 * @param imageResId идентификатор изображения экрана
 * @param description текстовое описание экрана
 * @param description текстовое описание экрана
 * @param hasButton наличие кнопки на экране
 * @param buttonIntent Intent кнопки
 * @param buttonAction кастомное действие по клику на кнопку
 * */
internal data class PageState(
    val isFeature: Boolean = true,
    @DrawableRes val imageResId: Int = ID_NULL,
    val description: String = "",
    val longestDescription: String = "",
    val hasButton: Boolean = false,
    val buttonText: String = "",
    val buttonIntent: Intent? = null,
    val buttonAction: (() -> Unit)? = null
) {
    companion object {
        val EMPTY = PageState()
    }
}