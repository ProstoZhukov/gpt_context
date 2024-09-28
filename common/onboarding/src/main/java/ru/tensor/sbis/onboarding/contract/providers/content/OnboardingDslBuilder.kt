package ru.tensor.sbis.onboarding.contract.providers.content

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.fragment.app.Fragment
import ru.tensor.sbis.verification_decl.permission.PermissionScope
import ru.tensor.sbis.onboarding.R

/**
 * Билдер компонента приветственного экрана
 *
 * @author as.chadov
 *
 * @property stickyIndicator true если статический баннер и индикатор пейджера фиксированы и не скролятся, по-умолчанию true
 * @property preventBackSwipe true если запрещен свайп назад по экранам фич, по-умолчанию false
 * @property backPressedSwipe true если осуществляется свайп назад по нажатию на кнопку "Назад", по-умолчанию false
 * @property swipeLeaving true если осуществляется выход с приветственного экран по свайпу последнего экрана фич, иначе false
 * @property useFlippingTimer true если страницы приветственного экрана будут перелистываться по таймеру, по-умолчанию false
 * @property finally целевое намерение после покидания приветственного экрана, по-умолчанию [MainActivityProvider]
 */
@Suppress("MemberVisibilityCanBePrivate")
class OnboardingBuilder : BaseDslBuilder<Onboarding>() {
    private var header = Header()
    private val pages = mutableListOf<Page>()

    @Deprecated("Больше не используется. Теперь по-умолчанию всегда true")
    var stickyIndicator = true
    var preventBackSwipe: Boolean = false
    var backPressedSwipe: Boolean = false
    var swipeLeaving: Boolean = true
    var useFlippingTimer: Boolean = false
    var finally: Intent? = null

    /**
     * описать область заголовка
     */
    fun header(init: HeaderBuilder.() -> Unit) {
        header = HeaderBuilder().apply(init)
            .build()
    }

    /**
     * добавить экран фичи
     */
    fun page(init: FeaturePageBuilder.() -> Unit) {
        pages.add(FeaturePageBuilder().apply(init).build())
    }

    /**
     * добавить экран отсутсвия прав на управляемую область полномочий
     */
    fun noPermissionPage(init: NoPermissionPageBuilder.() -> Unit) {
        pages.add(NoPermissionPageBuilder().apply(init).build())
    }

    /**
     * добавить пользовательский экран
     */
    fun customPage(init: CustomPageBuilder.() -> Unit) {
        pages.add(CustomPageBuilder().apply(init).build())
    }

    private fun appointIdentification(pages: List<Page>) =
        pages.mapIndexed { index, page -> page.apply { uuid = "$index" } }

    override fun build(): Onboarding = Onboarding(
        header = header,
        pages = appointIdentification(pages),
        targetIntent = finally,
        preventBackSwipe = preventBackSwipe,
        backPressedSwipe = backPressedSwipe,
        swipeLeaving = swipeLeaving,
        useFlippingTimer = useFlippingTimer
    )
}

/**
 * Билдер заголовка приветственного экрана
 *
 * @property textResId id ресурса теста в заголовке
 * @property imageResId id ресурса логотипа
 * @property gravityToBottom true если заголовок прижат по низу логотипа
 */
@Suppress("MemberVisibilityCanBePrivate")
class HeaderBuilder : BaseDslBuilder<Header>() {
    @StringRes
    var textResId: Int = ID_NULL
    @DrawableRes
    var imageResId: Int = ID_NULL
    var gravityToBottom: Boolean = true

    override fun build(): Header = Header(textResId, imageResId, gravityToBottom)
}

/**
 * Билдер экрана фичи
 *
 * @property permissionList системные зависимости
 * @property customAction пользовательское действие на экране
 * @property suppressed стратегия подавления функциональности экрана при отсутствии пра
 */
@Suppress("MemberVisibilityCanBePrivate")
class FeaturePageBuilder : BasePageBuilder<FeaturePage>() {
    private val permissionList: MutableList<String> = mutableListOf()
    private var customAction = CustomAction.EMPTY
    var suppressed: SuppressBehaviour = SuppressBehaviour.NOTHING

    /**
     * @param permission системные разрешения востребуемые описаной фичей
     */
    fun permission(permission: String) = permissionList.add(permission)

    /**
     * добавить пользовательское действие для экрана фичи
     */
    fun action(init: ActionBuilder.() -> Unit) {
        customAction = ActionBuilder().apply(init)
            .build()
    }

    override fun createDefaultButton() = Button(R.string.onboarding_start_work)

    override fun build() = FeaturePage(
        description = description,
        image = image,
        button = button,
        style = style,
        permissions = SystemPermissions(permissionList),
        action = customAction,
        suppressed = suppressed
    )
}

/**
 * Билдер экран отсутствия прав на область
 *
 * @property inclusiveStrategy true если экран отображается при отсутствии прав на все области, false на любую из областей
 */
@Suppress("MemberVisibilityCanBePrivate")
class NoPermissionPageBuilder : BasePageBuilder<NoPermissionPage>() {
    private val permissionScopes = mutableListOf<PermissionScope>()
    override var defaultButton: Boolean = true
    var inclusiveStrategy: Boolean = true

    /**
     * @param scopes области полномочий
     */
    fun permissionScopes(vararg scopes: PermissionScope) = permissionScopes.addAll(scopes)

    override fun createDefaultButton() = Button(R.string.onboarding_close)

    override fun build() = NoPermissionPage(
        description = description,
        image = image,
        button = button,
        style = style,
        permissionScopes = permissionScopes
    )
}

/**
 * Билдер пользовательского экрана
 *
 * @property creator объект создатель фрагмента
 * @property canSwipe коллбэк состояния навигации свайпом
 */
@Suppress("MemberVisibilityCanBePrivate")
class CustomPageBuilder : PageBuilder<CustomPage>() {
    var creator: (() -> Fragment)? = null
    var canSwipe: (() -> Boolean)? = null

    override fun build() = creator?.let { CustomPage(it, canSwipe ?: { true }) }
        ?: throw NullPointerException("Required 'creator' field is null")
}

/**
 * Билдер описания страницы [Description]
 */
@Suppress("MemberVisibilityCanBePrivate")
class DescriptionBuilder : BaseDslBuilder<Description>() {
    @StringRes
    var textResId: Int = ID_NULL
    var text: String = ""

    override fun build() = Description(textResId, text)
}

/**
 * Билдер изображения страницы [Image]
 */
@Suppress("MemberVisibilityCanBePrivate")
class ImageBuilder : BaseDslBuilder<Image>() {
    @DrawableRes
    var imageResId: Int = ID_NULL

    override fun build(): Image = Image(imageResId)
}

/**
 * Билдер пользовательского действия на экране фичи [BasePage]
 *
 * @property byLeave true действие инициируется при покидании экрана (сейчас единый подход)
 * @property isFinite true если действие конечно, т.е. инициируется единожды
 * @property execute само пользовательское действие (содержит аргумент колл-бэк информирования о
 * завершении пользовательского действия и его успешности)
 */
@Suppress("MemberVisibilityCanBePrivate")
class ActionBuilder : BaseDslBuilder<CustomAction>() {
    var byLeave: Boolean = true
    var isFinite: Boolean = true
    var execute: (postExecute: (Boolean) -> Unit) -> Unit = {}

    override fun build(): CustomAction = CustomAction(
        byLeave = byLeave,
        isFinite = isFinite,
        execute = execute
    )
}

/**
 * Билдер кнопки экрана [Button]
 *
 * @property textResId id ресурса заголовка
 * @property action обработчик действия по клику на кнопку
 * @property defaultAction true если применяем стандартный обработчик действия, иначе false
 */
@Suppress("MemberVisibilityCanBePrivate")
class ButtonBuilder : BaseDslBuilder<Button>() {
    @StringRes
    var textResId: Int = ID_NULL
    var action: (() -> Unit)? = null
    var defaultAction: Boolean = true

    override fun build(): Button {
        if (textResId == ID_NULL) {
            textResId = R.string.onboarding_start_work
        }
        return Button(
            textResId = textResId,
            defaultAction = action == null && defaultAction,
            action = action
        )
    }
}

/**
 * Билдер стиля экрана [Style]
 *
 * @property themeResId id ресурса темы [FeatureTheme](..\onboarding\src\main\res\values\theme.xml)
 * @property tabletThemeResId id ресурса темы под планшет [FeatureTheme.Dialog](..\onboarding\src\main\res\values\theme.xml)
 */
@Suppress("MemberVisibilityCanBePrivate")
class StyleBuilder : BaseDslBuilder<Style>() {
    @StyleRes
    var themeResId: Int = R.style.FeatureTheme

    @StyleRes
    var tabletThemeResId: Int = R.style.FeatureTheme_Dialog

    override fun build(): Style {
        return Style(
            themeResId,
            tabletThemeResId
        )
    }
}