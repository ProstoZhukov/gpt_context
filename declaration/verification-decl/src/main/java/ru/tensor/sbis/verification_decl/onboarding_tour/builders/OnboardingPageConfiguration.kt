package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера для создания конкретного экрана в [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingPageConfiguration {

    /**
     * Опциональный индекс/идентификатор экрана. Используется для:
     * 1. определения порядка отображения экранов.
     * В этом случае необходим только если порядок вхождения/регистрации экранов при описании DSL отличается от требуемого при визуализации.
     * 2. уникализации экранов.
     * В этом случае необходим только если нарушается консистентность данных при описании DSL.
     * Например: разные вызовы dsl-билдера могут возвращать экраны в разном кол-ве и порядке, тогда как по умолчанию ожидается что
     * порядок неизменный, а видимость экрана устанавливается посредством [checkIsRequired].
     */
    var position: Int

    /** Ресурс с заголовком экрана. */
    @get:StringRes
    var title: Int

    /** Ресурс с описанием экрана. */
    @get:StringRes
    var description: Int

    /** Ресурс изображения на экране. */
    @get:DrawableRes
    var image: Int

    /**
     * Ресурс подписи на кнопке перехода к следующему экрану. В случае отсутвия кнопка будет скрыта.
     * Для более тонкой настройки кнопки перехода используется [button].
     */
    @get:StringRes
    var nextButtonTitle: Int

    /** Стили кнопок. По умолчанию BrandButtonStyle. */
    var nextButtonStyle: SbisButtonStyle

    /** Установить специфичный баннер текущего экрана. */
    fun customBanner(init: OnboardingBannerConfiguration.() -> Unit)

    /** Установить расширенные настройки кнопки перехода текущего экрана. */
    fun button(init: OnboardingButtonConfiguration.() -> Unit)

    /** Установить подпись с условиями использования. */
    fun terms(init: OnboardingTermsConfiguration.() -> Unit)

    /** Установить запрос прав и разрешений. */
    fun permissions(init: OnboardingPermissionConfiguration.() -> Unit)

    /** Опциональный коллбэк проверки необходимости отображения экрана в туре онбординга. */
    fun checkIsRequired(callback: PageRequiredCallback)
}