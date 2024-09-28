package ru.tensor.sbis.verification_decl.onboarding

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Интерфейс описывающий публичное api модуля "Приветственного Экрана"
 *
 * @property action action intent приветственного экрана
 *
 * @author as.chadov
 */
interface OnboardingFeature : Feature {

    val action: String

    /**
     * Получить интент для запуска активности приветственного экрана
     *
     * @return интент на запуск активности приветственного экрана
     */
    fun getOnboardingActivityIntent(): Intent

    /**
     * Подменить намерение на активность приветственного экрана после актуализации
     * необходимости запуска комопнента
     *
     * @param origin исходноее намерение, по-умолчанию действие из [MainActivityProvider]
     * @param ignoreTablet игнорировать подмену под планшет, по-умолчанию true
     * @return намерение
     */
    fun substituteIntent(
        origin: Intent? = null,
        ignoreTablet: Boolean = true
    ): Intent

    /**
     * Стартовать непоказанный интент активности или фрагмент приветственного экрана
     *
     * @param activity контекст активности для запуска приветственного экрана
     * @param ignoreTablet игнорировать запуск под планшет, по-умолчанию false
     * @param dialogOnTablet true если для конфигурации планшета отображаем диалог, по-умолчанию true
     * @return true если приветственный экран будет запущен, false иначе
     */
    fun startNonShownOnboarding(
        activity: Context?,
        ignoreTablet: Boolean = false,
        dialogOnTablet: Boolean = true
    ): Boolean

    /**
     * Получить фрагмент приветственного экрана
     *
     * @return фрагмент приветственного экрана
     */
    fun getOnboardingFragment(): Fragment

    /**
     * Получить диалог фрагмент приветственного экрана в контейнере
     *
     * @return фрагмент приветственного экрана
     */
    @Deprecated(
        message = "Использовать showOnboardingDialogFragment или не забывать проверять доступность фрагмента в FragmentManager",
        replaceWith = ReplaceWith("showOnboardingDialogFragment")
    )
    fun getOnboardingDialogFragment(): DialogFragment

    /**
     * Показать диалог фрагмент приветственного экрана в контейнере
     */
    fun showOnboardingDialogFragment(manager: FragmentManager)

    /**
     * Получить тэг фрагмент приветственного экрана
     * Необходимо использовать при добавлении фрагмента [getOnboardingFragment],
     * [getOnboardingDialogFragment] в стэк вне онбординг активности
     *
     * @return тэг фрагмента приветственного экрана
     */
    fun getOnboardingFragmentStackTag(): String

    /**
     * Показывался ли уже ранее приветственный экран или другие доступные OnboardingProvider'ы,
     * в т.ч. для текущего пользователя, если доступен [LoginInterface].
     *
     * @return true если отображался, false иначе
     */
    fun isOnboardingShown(): Boolean

    /**
     * Показывался ли уже ранее приветственный тур, в т.ч. для текущего пользователя, если доступен [LoginInterface].
     * Речь именно о приветственном экране ("Что нового" не учитывается).
     *
     * @return true если отображался, false иначе
     */
    suspend fun isOnboardingTourShown(): Boolean

    /**
     * Завершен ли уже просмотр приветственного экрана или другие доступные OnboardingProvider'ы,
     * в т.ч. для текущего пользователя, если доступен [LoginInterface].
     * Метод может быть использован для проверки необходимости повторного отображения приветственного экрана,
     * если первый просмотр не был завершен должны образом (например неожиданной остановкой процесса).
     *
     * @return true если приветственный экран пройден, false иначе
     */
    suspend fun isOnboardingProcced(): Boolean

    /**
     * Возвращает навигатор по фрагментам "Приветственного Экрана"
     */
    fun getOnboardingNavigator(): OnboardingNavigator

    /**
     * Подписка на закрытие компонента "Приветственного Экрана"
     * Фактически "Приветсвенный экран" не был удален и поскольку это единственный фрагмент в стэке то
     * обработка удаления должна быть выполнена на клиенте
     */
    fun observeOnboardingDismiss(): Observable<Unit>

    /**
     * Подписка на закрытие компонента "Приветственного экрана".
     * Передает события закрытия компонента в любых ситуациях.
     * Подходит для выполнения кода, который должен выполниться после закрытия онбординга.
     * Событие будет передано даже, если подписаться после того, как онбординг уже закрыли.
     */
    fun observeOnboardingCloseEvent(): Observable<Unit>
}