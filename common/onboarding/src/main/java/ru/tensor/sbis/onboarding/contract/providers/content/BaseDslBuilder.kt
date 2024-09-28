package ru.tensor.sbis.onboarding.contract.providers.content

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL

/**
 * Базовый экран
 *
 * @author as.chadov
 *
 * @property descriptionResId идентификатор текста описания экрана
 * @property imageResId идентификатор основного изображения экрана
 * @property defaultButton true если необходимо отобразить кнопку по-умодчанию
 */
abstract class BasePageBuilder<T : BasePage> : PageBuilder<T>() {
    protected var description: Description = Description.EMPTY
        get() = if (field != Description.EMPTY) {
            field
        } else Description(descriptionResId)

    protected var image: Image = Image.EMPTY
        get() = if (field != Image.EMPTY) {
            field
        } else Image(imageResId)

    protected var button: Button = Button.EMPTY
        get() = when {
            field != Button.EMPTY -> field
            defaultButton         -> createDefaultButton()
            else                  -> field
        }
    protected var style: Style = Style.EMPTY
    @StringRes
    var descriptionResId = ID_NULL
    @DrawableRes
    var imageResId = ID_NULL
    open var defaultButton = false

    /**
     * добавить описание (оставлено для возможности расширения dsl, достаточно использовать [BasePageBuilder.descriptionResId])
     */
    fun description(init: DescriptionBuilder.() -> Unit) {
        description = DescriptionBuilder().apply(init)
                .build()
    }

    /**
     * добавить изображение (оставлено для возможности расширения dsl, достаточно использовать [BasePageBuilder.imageResId])
     */
    fun image(init: ImageBuilder.() -> Unit) {
        image = ImageBuilder().apply(init)
                .build()
    }

    /**
     * добавить кнопку (для использования стандартной релизации [BasePageBuilder.defaultButton])
     */
    fun button(init: ButtonBuilder.() -> Unit) {
        button = ButtonBuilder().apply(init)
                .build()
    }

    /**
     * добавить стиллизацию для конкретного экрана (чтобы стилизовать все экраны
     * установите тему для атрибута `onboardingFeatureTheme`)
     */
    fun style(init: StyleBuilder.() -> Unit) {
        style = StyleBuilder().apply(init)
                .build()
    }

    protected open fun createDefaultButton() = Button.EMPTY
}

abstract class PageBuilder<T : Page> : BaseDslBuilder<T>()

@OnboardingDslBuilderMarker
abstract class BaseDslBuilder<T> {
    internal abstract fun build(): T
}

@DslMarker
internal annotation class OnboardingDslBuilderMarker