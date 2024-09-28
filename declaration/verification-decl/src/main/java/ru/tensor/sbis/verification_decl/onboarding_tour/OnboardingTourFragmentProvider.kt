package ru.tensor.sbis.verification_decl.onboarding_tour

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Поставщик Приветственного экрана настроект приложения.
 *
 * @author as.chadov
 */
interface OnboardingTourFragmentProvider : Feature {

    /**
     * Получить фрагмент с первым на очереди активным туром Приветственного экрана настроект приложения.
     * @return null если нет активного тура или МП запущено в режиме автотестов (т.е. соответственно флаг isAutoTestLaunch должен уже быть актуальным).
     */
    suspend fun getNext(): Fragment?

    /**
     * Создать фрагмент Приветственного экрана настроект приложения.
     * @param tourName опциональное имя тура приветственного экрана [OnboardingTourProvider.name].
     * По умолчанию взято [OnboardingTourProvider.DEFAULT_NAME].
     *
     * @throws IllegalArgumentException если отсутсвет тур с именем [tourName]
     * @throws IllegalStateException если МП запущено в режиме автотестов
     */
    @Throws(
        IllegalArgumentException::class,
        IllegalStateException::class
    )
    fun create(tourName: OnboardingTour.Name = OnboardingTourProvider.DEFAULT_NAME): Fragment
}