package ru.tensor.sbis.onboarding.ui.host

import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.viewpager.widget.ViewPager
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.onboarding.domain.interactor.HostInteractor
import ru.tensor.sbis.onboarding.domain.interactor.usecase.HostState
import ru.tensor.sbis.onboarding.domain.util.FlipEvent
import ru.tensor.sbis.onboarding.domain.util.FlipTimer
import ru.tensor.sbis.onboarding.ui.banner.BannerVM
import ru.tensor.sbis.onboarding.ui.base.BaseViewModel
import ru.tensor.sbis.onboarding.ui.host.adapter.FeaturePageCreator
import ru.tensor.sbis.onboarding.ui.host.adapter.PageListHolder
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator
import ru.tensor.sbis.onboarding.ui.view.OnSwipeListener
import ru.tensor.sbis.onboarding.ui.view.SwipeDelegate
import javax.inject.Inject

/**
 * Вью-модель экрана приветствия
 *
 * @author as.chadov
 *
 * @param bannerVm вью-модель шапки экрана/экранов фичи или заглушки
 * @param flipTimer таймер для перелистывания страниц приветственного экрана
 *
 * @property isPreventBack скролл назад в пейджере
 * @property longestDescription наиболее длинное описание фичи
 * @property stubImage идентификатор изображения фичи для placeholder
 * @property isFlipProgressShown видимость прогресса отсчета на перелистывание страницы
 * @property flipProgress прогресс отсчета в интервале от 0 до [maxFlipProgress]
 * @property maxFlipProgress максимальный прогресс = интервал перелистывания в мс
 */
internal class OnboardingHostVM @Inject constructor(
    val bannerVm: BannerVM,
    private val interactor: HostInteractor,
    private val router: OnboardingHostRouter,
    private val swipeDelegate: SwipeDelegate,
    private val flipTimer: FlipTimer,
    providerMediator: OnboardingProviderMediator,
    preferenceManager: OnboardingPreferenceManager
) : BaseViewModel(),
    PageListHolder.Provider,
    FeaturePageCreator.Provider,
    OnboardingContract,
    OnSwipeListener by swipeDelegate {

    val isPreventBack = ObservableBoolean()
    val longestDescription = ObservableField<String>()
    val stubImage = ObservableInt(0)
    val isFlipProgressShown = ObservableBoolean(false)
    val flipProgress = ObservableInt()
    val maxFlipProgress = ObservableInt()
    private var hostState = HostState.EMPTY

    init {
        addDisposable { interactor }
        observeHostState()
        providerMediator.getActiveProvider()
            ?.getCustomOnboardingPreferenceManger()
            ?.saveEntrance()
            ?: preferenceManager.saveEntrance()
    }

    override val holder: PageListHolder = interactor
    override val creator: FeaturePageCreator = interactor
    private var timerDisposable: Disposable? = null
    private var isTimerRunning = false
    private var originOrientation: Int? = null

    override fun onSwipeForward(leavePosition: Int, deferredSwipeAction: ViewPager.() -> Unit): Boolean {
        if (interactor.canSwipe(leavePosition)) {
            return swipeDelegate.onSwipeForward(leavePosition, deferredSwipeAction)
        }
        return true
    }

    fun saveOriginOrientation(orientation: Int) {
        originOrientation = orientation
    }

    fun retainOriginOrientation(): Int? = originOrientation

    override fun onSwipeOutAtEnd(deferredSwipeAction: () -> Unit) =
        swipeDelegate.onSwipeOutAtEnd(::onCloseOnboarding)

    override fun onCloseOnboarding() = addDisposable {
        interactor.observeTargetIntent()
            .subscribe(router::dismiss)
    }

    /**
     * @return true если поддерживается свайп назад по нажатию на кнопку "Назад"
     */
    fun canSwipeBackPressed(): Boolean = hostState.isBackPressed

    fun startFlipTimerIfNeed() {
        if (hostState.isAutoSwitchable.not() || isTimerRunning) {
            return
        }
        isTimerRunning = true
        timerDisposable = flipTimer.observeFlip(
            ::showFlipProgress,
            ::hideFlipProgress
        ).subscribe(::processFlipEvent)

    }

    fun stopFlipTimerIfNeed() {
        timerDisposable?.dispose()
        timerDisposable = null
        isTimerRunning = false
    }

    /**
     * Уведомить о смене страницы
     *
     * @param position позиция текущей выбранной страницы
     */
    fun notifyOnPageChange(position: Int) {
        val imageResId = interactor.getCurrentBroadsheet(position)
        if (imageResId != ID_NULL) {
            stubImage.set(imageResId)
        }
    }

    override fun onCleared() {
        bannerVm.dispose()
        super.onCleared()
    }

    private fun observeHostState() = addDisposable {
        interactor.observeHostState()
            .subscribe { state ->
                with(state) {
                    hostState = this
                    stubImage.set(firstImageResId)
                    processHostState()
                    startFlipTimerIfNeed()
                }
            }
    }

    private fun HostState.processHostState() {
        isPreventBack.set(isPreventBackSwipe)
        longestDescription.set(longestDescriptionText)
        bannerVm.setButtonState(true)
    }

    private fun processFlipEvent(event: FlipEvent) {
        flipProgress.set(event.progress)
        if (event.isCompleted) {
            router.turnPage()
        }
    }

    private fun showFlipProgress() {
        maxFlipProgress.set(flipTimer.flipDelay)
        isFlipProgressShown.set(true)
    }

    private fun hideFlipProgress() = isFlipProgressShown.set(false)
}