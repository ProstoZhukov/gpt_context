package ru.tensor.sbis.onboarding.ui.host

import androidx.annotation.Px

/**
 * Слушатель изменения границ изображения фичи
 * Используется для позиционирования элементов пейджера [OnboardingHostFragmentImpl] относительно изображения
 * фичи [FeatureFragment]
 */
internal interface ImageFrameListener {

    /**
     * Обработать изменения границ
     */
    fun onChangeFrame(@Px height: Int)
}