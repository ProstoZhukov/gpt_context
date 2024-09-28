package ru.tensor.sbis.onboarding.ui.page

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import ru.tensor.sbis.onboarding.domain.interactor.FeatureInteractor
import ru.tensor.sbis.onboarding.domain.interactor.usecase.PageState
import ru.tensor.sbis.onboarding.ui.banner.BannerVM
import ru.tensor.sbis.onboarding.ui.base.BaseViewModel
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostRouter
import javax.inject.Inject

/**
 * Вью-модель конкретной фичи или заглушки
 *
 * @author as.chadov
 *
 * @param bannerVm вью-модель шапки экрана/экранов фичи или заглушки
 * @param interactor интерактор конкретной фичи
 * @param router интерфейс роутера приветственного экрана
 *
 * @property description текст описания фичи
 * @property image идентификатор изображения фичи
 * @property isButtonShown видимость кнопки фичи
 * @property buttonText текст на кнопке фичи
 */
internal class OnboardingFeatureVM @Inject constructor(
    val bannerVm: BannerVM,
    val interactor: FeatureInteractor,
    private val router: OnboardingHostRouter,
) : BaseViewModel() {

    val description = ObservableField<String>()
    val longestDescription = ObservableField<String>()
    val image = ObservableInt()
    val isButtonShown = ObservableBoolean(false)
    val buttonText = ObservableField<String>()
    private var pageState = PageState.EMPTY

    init {
        requestPageState()
    }

    /**
     * Действие по клику на кнопку фичи
     */
    fun onButtonClick() {
        val (action, intent) = pageState.buttonAction to pageState.buttonIntent
        if (action != null) {
            action()
        } else if (intent != null) {
            if (pageState.isFeature) {
                interactor.askPotentialRequirement {
                    router.dismiss(intent)
                }
            } else {
                router.dismiss(intent)
            }
        }
    }

    override fun onCleared() {
        bannerVm.dispose()
        super.onCleared()
    }

    private fun requestPageState() = addDisposable {
        interactor.observePageState()
            .subscribe {
                pageState = it
                processBannerState()
                showPageContent()
                showButton()
            }
    }

    private fun processBannerState() =
        bannerVm.setButtonState(false)

    private fun showPageContent() {
        image.set(pageState.imageResId)
        description.set(pageState.description)
        longestDescription.set(pageState.longestDescription)
    }

    private fun showButton() {
        if (pageState.hasButton) {
            isButtonShown.set(true)
            buttonText.set(pageState.buttonText)
        }
    }
}