package ru.tensor.sbis.onboarding.ui.host

import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider

/**
 * Интерфейс получения нужных данных для работы с customPage.
 *
 * @author ps.smirnyh
 */
interface OnboardingHostFragment {

    /**
     * Получение провайдера тем onboarding для взаимодействия в customPage.
     */
    val themeProvider: ThemeProvider

    /**
     * Получение viewModel хоста onboarding для взаимодействия в customPage.
     */
    val getViewModel: OnboardingContract
}