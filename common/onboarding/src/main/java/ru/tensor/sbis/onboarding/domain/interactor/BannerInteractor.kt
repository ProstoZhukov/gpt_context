package ru.tensor.sbis.onboarding.domain.interactor

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import io.reactivex.Observable
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.onboarding.R
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.di.HostScope
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.domain.interactor.usecase.BannerState
import javax.inject.Inject

/**
 * Интерактор Заголовка экрана конкретной фичи или заглушки
 *
 * @author as.chadov
 *
 * @param repository репозиторий содержимого приветственного экрана
 * @param intentProvider провайдер главного намерения приложения
 */
@HostScope
internal class BannerInteractor @Inject constructor(
    private val repository: OnboardingRepository,
    private val intentProvider: MainActivityProvider
) {

    /**
     * @return состояние заголовка экрана
     */
    fun observeBannerState(): Observable<BannerState> =
        repository.observe()
            .map {
                BannerState(
                    titleResId = it.getTitleRes(),
                    titleGravityBias = it.getTitleGravityBias(),
                    logoResId = it.getLogo(),
                    buttonIntent = it.getTargetIntent()
                )
            }

    private fun Onboarding.getTitleRes() = header.textResId
        .takeIf { it != ID_NULL } ?: R.string.onboarding_empty_title

    private fun Onboarding.getTitleGravityBias(): Float =
        if (header.gravityToBottom) {
            1F
        } else {
            0.5F
        }

    @DrawableRes
    private fun Onboarding.getLogo() = header.imageResId

    private fun Onboarding.getTargetIntent() = targetIntent
        ?: intentProvider.getMainActivityIntent()
}