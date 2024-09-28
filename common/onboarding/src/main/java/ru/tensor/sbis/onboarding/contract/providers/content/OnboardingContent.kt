package ru.tensor.sbis.onboarding.contract.providers.content

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.fragment.app.Fragment
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Содержимое компонента приветственного экрана, [OnboardingBuilder]
 *
 * @author as.chadov
 *
 * @property header заголовок приветственного экрана
 * @property pages страницы приветственного экрана
 * @property targetIntent целевое намерение после покидания приветственного экрана, по-умолчанию [MainActivityProvider]
 * @property preventBackSwipe true если запрещен свайп назад по экранам фич, по-умолчанию false
 * @property backPressedSwipe true если осуществляется свайп назад по нажатию на кнопку "Назад", по-умолчанию false
 * @property swipeLeaving true если осуществляется выход с приветственного экран по свайпу последнего экрана фич, иначе false
 * @property useFlippingTimer true если страницы приветственного экрана будут перелистываться по таймеру
 */
data class Onboarding internal constructor(
    val header: Header = Header(),
    val pages: List<Page> = mutableListOf(),
    val targetIntent: Intent? = null,
    val preventBackSwipe: Boolean = false,
    val backPressedSwipe: Boolean = false,
    val swipeLeaving: Boolean = true,
    val useFlippingTimer: Boolean = false
) {
    companion object {
        internal val EMPTY = Onboarding()

        fun create(init: OnboardingBuilder.() -> Unit): Onboarding {
            return OnboardingBuilder().run {
                init()
                build()
            }
        }

        /**
         * Метод декларативного объявления содержимого компонента приветственного экрана
         */
        @JvmSynthetic
        operator fun invoke(init: OnboardingBuilder.() -> Unit) = create(init)
    }
}

/**
 * Заголовок приветственного экрана, [HeaderBuilder]
 */
data class Header internal constructor(
    @StringRes
    var textResId: Int = ID_NULL,
    @DrawableRes
    var imageResId: Int = ID_NULL,
    var gravityToBottom: Boolean = true
)

/**
 * Пустая страница приветственного экрана, [HeaderBuilder]
 */
interface Page {
    var uuid: String
}

/**
 * Базовая страница приветственного экрана, [BasePageBuilder]
 */
interface BasePage : Page {
    val description: Description
    val image: Image
    val style: Style
    var button: Button
}

/**
 * Фич-страница приветственного экрана, [FeaturePageBuilder]
 */
data class FeaturePage internal constructor(
    override val description: Description,
    override val image: Image,
    override var button: Button,
    override val style: Style,
    var permissions: SystemPermissions = SystemPermissions.EMPTY,
    var action: CustomAction = CustomAction.EMPTY,
    val suppressed: SuppressBehaviour = SuppressBehaviour.NOTHING
) : BasePage {
    override var uuid: String = ""

    companion object {
        internal fun emptyInstance() = FeaturePage(
            Description.EMPTY,
            Image.EMPTY,
            Button.EMPTY,
            Style.EMPTY
        )
    }
}

/**
 * Страница заглушка об отсутствии прав, [NoPermissionPageBuilder]
 */
data class NoPermissionPage internal constructor(
    override val description: Description,
    override val image: Image,
    override var button: Button,
    override val style: Style,
    val permissionScopes: List<PermissionScope> = emptyList(),
    val inclusiveStrategy: Boolean = true
) : BasePage {
    override var uuid: String = ""
}

/**
 * Кастомная страница приветственного экрана, [CustomPageBuilder]
 */
open class CustomPage internal constructor(
    var creator: (() -> Fragment),
    var canSwipe: (() -> Boolean) = { true }
) : Page {
    override var uuid: String = ""
}

/**
 * Кнопка страницы, [ButtonBuilder]
 */
data class Button internal constructor(
    @StringRes
    var textResId: Int = ID_NULL,
    val defaultAction: Boolean = true,
    var action: (() -> Unit)? = null
) {
    internal companion object {
        val EMPTY = Button()
    }
}

/**
 * Описание страницы (оставлено для возможности расширения dsl), [DescriptionBuilder]
 */
data class Description internal constructor(
    @StringRes
    var textResId: Int = ID_NULL,
    var text: String = ""
) {
    internal companion object {
        val EMPTY = Description()
    }
}

/**
 * Изображение страницы (оставлено для возможности расширения dsl), [ImageBuilder]
 */
data class Image internal constructor(
    @DrawableRes
    var imageResId: Int = ID_NULL
) {
    internal companion object {
        val EMPTY = Image()
    }
}

/**
 * Стили страницы, [StyleBuilder]
 */
data class Style internal constructor(
    @StyleRes
    val themeResId: Int,
    @StyleRes
    val tabletThemeResId: Int
) {
    internal companion object {
        val EMPTY = Style(0, 0)
    }
}

/**
 * Набор системных разрешений, запрашиваемых на странице
 *
 * @param values список констант с системными зависимостями
 * @property isProcessed true если набор уже был обработан, запрошен
 * @property isProcessing true если набор уже на обработке
 */
data class SystemPermissions internal constructor(
    val values: List<String>
) {
    var isProcessed: Boolean = false
        set(value) {
            field = value
            if (value) {
                isProcessing = false
            }
        }
    var isProcessing: Boolean = false

    val isEmpty: Boolean
        get() = values.isEmpty()

    internal companion object {
        val EMPTY = SystemPermissions(emptyList())
            .also { it.isProcessed = true }
    }
}

/**
 * Пользовательское действие на экране фичи, [ActionBuilder]
 *
 * @property processed true если действие имело место
 */
data class CustomAction internal constructor(
    var byLeave: Boolean = true,
    var isFinite: Boolean = true,
    var execute: (postExecute: (Boolean) -> Unit) -> Unit
) {
    internal var processed = false

    internal companion object {
        val EMPTY = CustomAction(true, true) {}
    }
}

/**
 * Стратегия подавления функциональности экрана при отсутствии прав
 */
enum class SuppressBehaviour {
    NOTHING,
    BUTTON,
    SCREEN,
    PERMISSION
}