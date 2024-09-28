package ru.tensor.sbis.onboarding_tour.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingPageConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageRequiredCallback

/**
 * Одна страница онбординга или экрана настроек.
 * Подробнее [OnboardingPageConfiguration].
 *
 * @author as.chadov
 */
internal data class TourPage(
    val id: Int,
    val position: Int,
    @StringRes
    val titleResId: Int,
    @StringRes
    val descriptionResId: Int,
    @DrawableRes
    val imageResId: Int,
    var requiredCommand: PageRequiredCallback,
    var banner: TourBanner,
    var button: TourButton,
    val terms: TourTerms?,
    val permissions: TourPermissions?
) {

    companion object {
        /**@SelfDocumented */
        const val FIRST_POSITION = 0
    }
}