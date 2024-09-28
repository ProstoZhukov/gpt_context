package ru.tensor.sbis.onboarding.domain.interactor.usecase

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.onboarding.R

/**
 * Модель описывающая основные атрибуты состояния шапки экранов
 *
 * @author as.chadov
 */
internal data class BannerState(
    @StringRes val titleResId: Int = R.string.onboarding_empty_title,
    val titleGravityBias: Float = 0.0F,
    @DrawableRes val logoResId: Int = ID_NULL,
    val buttonIntent: Intent? = null
) {
    companion object {
        val EMPTY = BannerState()
    }
}