package ru.tensor.sbis.onboarding.domain.interactor

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import io.reactivex.Observable
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.onboarding.contract.providers.content.BasePage
import ru.tensor.sbis.onboarding.contract.providers.content.Button
import ru.tensor.sbis.onboarding.contract.providers.content.NoPermissionPage
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.di.FeatureScope
import ru.tensor.sbis.onboarding.di.ui.page.ARG_FEATURE_UUID
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.domain.interactor.usecase.PageState
import ru.tensor.sbis.onboarding.domain.provider.StringProvider
import ru.tensor.sbis.onboarding.domain.util.PermissionHelper
import javax.inject.Inject
import javax.inject.Named

/**
 * Интерактор экрана конкретной фичи или заглушки
 *
 * @author as.chadov
 *
 * @param uuid идентификатор экрана
 * @param repository репозиторий содержимого приветственного экрана
 * @param stringProvider провайдер текстовых ресурсов
 * @param intentProvider провайдер главного намерения приложения
 * @param permissionHelper хелпер для работы с разрешениями приветственного экрана
 */
@FeatureScope
internal class FeatureInteractor @Inject constructor(
    @Named(ARG_FEATURE_UUID) private val uuid: String,
    private val repository: OnboardingRepository,
    private val stringProvider: StringProvider,
    private val intentProvider: MainActivityProvider,
    private val permissionHelper: PermissionHelper
) {
    /**
     * Синхронно возвращает состояние страницы из кэша
     *
     * @return состояние страницы
     */
    fun observePageState(): Observable<PageState> =
        repository.observe().map {
            val page = repository.findDeclaredPage(uuid)
            transformPageState(it, page)
        }

    /**
     * Обработать возможные запросы системных разрешений для экрана
     *
     * @finalAction действие по завершении обработки разрешений
     */
    fun askPotentialRequirement(finalAction: () -> Unit) {
        if (permissionHelper.hasUnresolvedPermissions(uuid)) {
            permissionHelper.askPermissionsAndAction(uuid) {
                finalAction()
            }
        } else finalAction()
    }

    private fun transformPageState(
        attrs: Onboarding,
        page: BasePage
    ): PageState {
        val longestText = stringProvider.findLongestString(repository.getCachedContent().pages)
        val buttonText = page.getButtonText()
        return PageState(
            page.isFeature(),
            page.getImage(),
            page.getDescription(),
            longestText,
            page.button != Button.EMPTY,
            buttonText,
            attrs.getButtonIntent(),
            page.button.action
        )
    }

    private fun Onboarding.getButtonIntent() = targetIntent
        ?: intentProvider.getMainActivityIntent()

    private fun BasePage.isFeature() = this !is NoPermissionPage

    @DrawableRes
    private fun BasePage.getImage() = image.imageResId
        .takeIf { it != ID_NULL }
        ?: throw IllegalArgumentException("Required 'imageResId' argument of the onboarding is missing")

    private fun BasePage.getDescription() = description.let {
        when {
            it.textResId != ID_NULL -> stringProvider.getString(it.textResId)
            it.text.isNotBlank()    -> it.text
            else                    -> ""
        }
    }

    private fun BasePage.getButtonText() = button.textResId
        .takeIf { it != ID_NULL }
        ?.let { stringProvider.getString(it) }
        .orEmpty()

}