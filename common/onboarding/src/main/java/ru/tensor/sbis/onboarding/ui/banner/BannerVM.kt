package ru.tensor.sbis.onboarding.ui.banner

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.onboarding.domain.interactor.BannerInteractor
import ru.tensor.sbis.onboarding.domain.interactor.usecase.BannerState
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostRouter
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostVM
import ru.tensor.sbis.onboarding.ui.page.OnboardingFeatureVM
import ru.tensor.sbis.onboarding.ui.utils.plusAssign
import javax.inject.Inject

/**
 * Вью-модель шапки для экранов фичи используемая в качестве баннера или невидимого
 * плейсхолдера в [OnboardingFeatureVM] и [OnboardingHostVM]
 *
 * @author as.chadov
 *
 * @property titleRes заголовок
 * @property titleBias смещение по оси y для выравнивания заголовка
 * @property compressed true если текст должен быть сжат к базовой линии для корректного выравнивания по оси y
 * @property logoRes иденетификатор логотипа
 * @property isButtonShown true если отображается кнопка закрытия
 */
internal class BannerVM @Inject constructor(
    private val interactor: BannerInteractor,
    private val router: OnboardingHostRouter,
    private val intentProvider: MainActivityProvider
) : Disposable {

    val logoRes = ObservableInt()
    val titleRes = ObservableInt()
    val titleBias = ObservableFloat()
    val compressed = ObservableBoolean(true)
    val isButtonShown = ObservableBoolean(false)
    private val disposables = CompositeDisposable()
    private var bannerState = BannerState.EMPTY

    init {
        requestState()
    }

    /**
     * Действие по клику на кнопку "закрыть" заголовка экрана
     */
    fun onCloseClick() {
        val intent = bannerState.buttonIntent ?: intentProvider.getMainActivityIntent()
        router.dismiss(intent)
    }

    /**
     * Установить состояние видимости кнопки "закрыть"
     */
    fun setButtonState(isShown: Boolean) {
        isButtonShown.set(isShown)
    }

    override fun dispose() = disposables.dispose()

    override fun isDisposed() = disposables.isDisposed

    private fun requestState() {
        disposables += interactor.observeBannerState()
            .subscribe(::processState)
    }

    private fun processState(state: BannerState) {
        bannerState = state
        state.run {
            titleRes.set(titleResId)
            titleBias.set(titleGravityBias)
            compressed.set(titleGravityBias == BOTTOM_ALIGNMENT)
            logoRes.set(logoResId)
        }
    }
}

private const val BOTTOM_ALIGNMENT = 1F