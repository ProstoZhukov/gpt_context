package ru.tensor.sbis.modalwindows.movable_container

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.base_components.fragment.StatusBarColorKeeper
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.utils.extentions.setTopMargin
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.FitToContent
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.Percent
import ru.tensor.sbis.design_dialogs.movablepanel.PanelWidth
import ru.tensor.sbis.design_dialogs.movablepanel.isEqual
import ru.tensor.sbis.design_dialogs.movablepanel.isNotEqual
import ru.tensor.sbis.modalwindows.R
import timber.log.Timber
import ru.tensor.sbis.design.design_dialogs.R as RDesignDialogs

/**
 * Интерфейс делегата для реализации экрана-обертки компонента шторка
 *
 * @author ga.malinskiy
 */
interface ContainerMovableDelegate :
    Container.Closeable,
    Container.Showable,
    Lockable,
    ForceCloseable,
    BackgroundChangeable,
    StatusBarColorKeeper {

    /**
     * Колбэк об изменении состояния шторки, открыта или нет
     */
    var stateCallback: (Boolean) -> Unit
    /**
     * Колбэк об изменении вертикальной позиции шторки
     */
    var slideCallback: (slidePosition: Float) -> Unit

    /**
     * Реакция для закрытия экрана
     */
    var popBackStack: () -> Unit

    /**
     * Создание рутового отображения
     */
    fun createView(inflater: LayoutInflater, container: ViewGroup?, arguments: Bundle?): View?

    /**
     * Событие после создания отображения
     */
    fun viewCreated(
        rootView: View,
        activity: FragmentActivity?,
        childFragmentManager: FragmentManager
    )

    /**
     * Уничтожение отображения
     */
    fun destroyView()

    /**
     * Уничтожение объекта
     */
    fun destroy()

    /**
     * Событие о необходимости закрытия
     */
    fun backPressed()

    /**
     * Запрос контенту на закрытие
     */
    fun requestCloseContent()

    /**
     * Установить тип высоты
     */
    fun setPeekHeight(peekHeightType: ContainerMovableDelegateImpl.PeekHeightType)

    /**
     * При конфликте скроллящихся вью на экране, вручную поставить необходимый.
     */
    fun setCurrentScrollViewProvider(provider: () -> View?)
}

/**
 * Реализация делегата экрана-обертки компонента шторка
 *
 * @param decorFitsSystemWindows используется ли системное декарировние окна, true если да, см. [WindowCompat.setDecorFitsSystemWindows]
 *
 * @author ga.malinskiy
 */
class ContainerMovableDelegateImpl(private val decorFitsSystemWindows: Boolean) : ContainerMovableDelegate {

    companion object {
        private const val CONTENT_CREATOR_ARG = ":CONTENT_CREATOR_ARG"
        private const val EXPANDED_PEEK_HEIGHT_ARG = "EXPANDED_PEEK_HEIGHT_ARG"
        private const val PEEK_HEIGHT_PARAMS_ARG = "PEEK_HEIGHT_PARAMS_ARG"
        private const val CONTAINER_BACKGROUND_COLOR_RES_ARG = "CONTAINER_BACKGROUND_COLOR_RES_ARG"
        private const val CONTAINER_BACKGROUND_COLOR_ARG = "CONTAINER_BACKGROUND_COLOR_ARG"
        private const val INSTANT_SHOW_CONTENT_ARG = "INSTANT_SHOW_CONTENT_ARG"
        private const val DEFAULT_HEADER_PADDING_ENABLED_ARG = "DEFAULT_HEASDER_PADDING_ENABLED_ARG"
        private const val MOVABLE_PANEL_THEME_ARG = "MOVABLE_PANEL_THEME_ARG"
        private const val IGNORE_OPEN_ANIM = "IGNORE_OPEN_ANIM"
        private const val AUTO_CLOSEABLE = "AUTO_CLOSEABLE"
        private const val CUSTOM_CLOSE_PANEL_ACTION = "CUSTOM_CLOSE_PANEL_ACTION"
        private const val IGNORE_LOCK = "IGNORE_LOCK"
        private const val PANEL_WIDTH_FOR_LANDSCAPE = "PANEL_WIDTH_FOR_LANDSCAPE"
        private const val FORCE_CLOSE_ON_BACK_PRESSED = "FORCE_CLOSE_ON_BACK_PRESSED"
        private const val SET_BOTTOM_PADDING = "SET_BOTTOM_PADDING"
        private const val HIDE_KEYBORAD_ON_START = "HIDE_KEYBORAD_ON_START"
        internal const val SOFT_INPUT_MODE = "SOFT_INPUT_MODE"
        internal const val CLOSE_ON_SHADOW_CLICK = "CLOSE_ON_SHADOW_CLICK"
        internal const val SHADOW_ENABLED = "SHADOW_ENABLED"

        private const val NIL_ID_INT = -1

        /**
         * По умолчанию контент отображается сразу.
         */
        private const val DEFAULT_INSTANT_SHOW_CONTENT_VALUE = true

        /**
         * По умолчанию контент отображается сразу.
         */
        private const val DEFAULT_HEADER_PADDING_ENABLED_VALUE = true
    }

    override var stateCallback: (Boolean) -> Unit = {}
    override var slideCallback: (Float) -> Unit = {}
    override var popBackStack: () -> Unit = {}

    private var view: View? = null
    private var activity: FragmentActivity? = null
    private var arguments: Bundle? = null
    private var childFragmentManager: FragmentManager? = null

    private val movablePanel: MovablePanel?
        get() = view as MovablePanel?

    private val innerContainerId: Int = R.id.modalwindows_movable_panel_container_id

    private var expandedPeekHeight: MovablePanelPeekHeight = Percent(.8F)
    private var hiddenPeekHeight: MovablePanelPeekHeight = Percent(0F)
    private var initHeight: MovablePanelPeekHeight = expandedPeekHeight

    private var disposer = CompositeDisposable()

    private var innerFragmentAdded = false

    private var isRequestPopBack: Boolean = false

    private var topInset = 0
    private var bottomInset = 0

    private var setBottomPaddingOnSlideNeeded = true

    override fun isStatusBarLightMode(): Boolean = false

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, arguments: Bundle?): View {
        this.arguments = arguments
        return requireArguments()
            .getInt(MOVABLE_PANEL_THEME_ARG, RDesignDialogs.style.MovablePanelDefaultTheme)
            .let {
                inflater.cloneInContext(
                    ThemeContextBuilder(
                        inflater.context,
                        defStyleAttr = RDesignDialogs.attr.MovablePanelTheme,
                        defaultStyle = it
                    ).build()
                )
            }
            .inflate(R.layout.modalwindows_movable_view_fragment, container, false)
    }

    override fun viewCreated(
        rootView: View,
        activity: FragmentActivity?,
        childFragmentManager: FragmentManager
    ) {
        view = rootView
        this.activity = activity
        this.childFragmentManager = childFragmentManager

        initViews()

        movablePanel?.ignoreLock = requireArguments().getBoolean(IGNORE_LOCK, false)

        if (requireArguments().getBoolean(HIDE_KEYBORAD_ON_START, true)) {
            activity?.currentFocus?.let { KeyboardUtils.hideKeyboard(it) }
        }

        movablePanel?.contentContainer?.let { contentContainer ->
            ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { v: View, insets: WindowInsetsCompat ->
                val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                topInset = systemBarsInsets.top

                val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                bottomInset = navBarInsets.bottom

                val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                val keyboardHeight = imeInsets.bottom

                onKeyboardHeightChanged(keyboardHeight)
                if (decorFitsSystemWindows.not()) {
                    if (keyboardHeight == 0) {
                        val padding = if (expandedPeekHeight !is FitToContent) getBottomPadding() else bottomInset
                        v.setBottomPadding(padding)
                    }
                    v.setTopMargin(navBarInsets.top)

                    val margin =
                        rootView.resources.getDimensionPixelSize(RDesignDialogs.dimen.movable_panel_layout_offset_12)

                    when (movablePanel?.panelWidth) {
                        PanelWidth.START_HALF ->
                            movablePanel?.movablePanelContainer?.updateLayoutParams<FrameLayout.LayoutParams> {
                                marginStart = navBarInsets.left + margin
                            }

                        PanelWidth.END_HALF ->
                            movablePanel?.movablePanelContainer?.updateLayoutParams<FrameLayout.LayoutParams> {
                                marginEnd = navBarInsets.right + margin
                            }

                        PanelWidth.MATCH_PARENT -> movablePanel?.movablePanelContainer?.updateLayoutParams<FrameLayout.LayoutParams> {
                            marginEnd = navBarInsets.right
                            marginStart = navBarInsets.left
                        }

                        else -> Unit
                    }
                }
                movablePanel?.movablePanelContainer?.updateLayoutParams<FrameLayout.LayoutParams> {
                    topMargin = topInset
                }
                insets
            }
        }
    }

    override fun destroyView() {
        view = null
        activity = null
    }

    override fun destroy() {
        disposer.dispose()
    }

    override fun lockPanel(locked: Boolean) {
        movablePanel?.isBehaviorLocked = locked
    }

    override fun setPeekHeight(peekHeightType: PeekHeightType) {
        movablePanel?.peekHeight =
            when (peekHeightType) {
                PeekHeightType.HIDDEN -> hiddenPeekHeight
                PeekHeightType.INIT -> initHeight
                PeekHeightType.EXPANDED -> expandedPeekHeight
                PeekHeightType.DEFAULT -> initHeight
            }
    }

    override fun setCurrentScrollViewProvider(provider: () -> View?) {
        movablePanel?.provideCurrentScrollView = provider
    }

    private fun initViews() {
        val panelWidth = requireArguments().getParcelableUniversally(PANEL_WIDTH_FOR_LANDSCAPE) as? PanelWidth
        if (panelWidth != null && DeviceConfigurationUtils.isLandscape(activity!!))
            movablePanel?.panelWidth = panelWidth

        val peekHeightParams = requireArguments().getParcelableArrayList<PeekHeightParams>(PEEK_HEIGHT_PARAMS_ARG)

        var peekHeightList = arrayListOf<MovablePanelPeekHeight>()

        if (peekHeightParams.isNullOrEmpty()) {
            expandedPeekHeight =
                requireArguments().getParcelableUniversally(EXPANDED_PEEK_HEIGHT_ARG) ?: expandedPeekHeight
            initHeight = expandedPeekHeight
            peekHeightList = arrayListOf(expandedPeekHeight, hiddenPeekHeight)
        } else {
            peekHeightParams.forEach {
                val height = it.peekHeight
                peekHeightList.add(height)
                when (it.type) {
                    PeekHeightType.INIT -> initHeight = height
                    PeekHeightType.HIDDEN -> hiddenPeekHeight = height
                    PeekHeightType.EXPANDED -> expandedPeekHeight = height
                    else -> Unit
                }
            }
        }

        val initPanelHeight = if (isInstantShowContent()) initHeight else hiddenPeekHeight

        addInnerFragmentIfNeeded()

        movablePanel?.apply {
            ignoreOpenAnim = isOpenAnimIgnored()
            defaultHeaderPaddingEnabled =
                requireArguments().getBoolean(DEFAULT_HEADER_PADDING_ENABLED_ARG, DEFAULT_HEADER_PADDING_ENABLED_VALUE)
            val closeOnShadowClick = requireArguments().getBoolean(CLOSE_ON_SHADOW_CLICK, true)
            setOnShadowClickListener {
                if (closeOnShadowClick) {
                    customClosePanelAction()?.action?.invoke()
                    hidePanel()
                }
            }

            setPeekHeightList(peekHeightList, initPanelHeight)

            setShadowEnabled(requireArguments().getBoolean(SHADOW_ENABLED, true))
            val isAutoCloseable = isAutoCloseable()
            var lastPeekHeight = hiddenPeekHeight
            disposer += getPanelStateSubject().subscribe {
                stateCallback(it.isEqual(hiddenPeekHeight).not())
                if (it.isEqual(hiddenPeekHeight) && isAutoCloseable && isRequestPopBack.not()) {
                    if (isNestedFragmentReadyToClose()) {
                        customClosePanelAction()?.action?.invoke()
                        isRequestPopBack = true
                        requestPopBack()
                    } else {
                        peekHeight = lastPeekHeight
                    }
                }
                lastPeekHeight = it
            }
            setBottomPaddingOnSlideNeeded = requireArguments().getBoolean(SET_BOTTOM_PADDING, true)
            disposer += getPanelSlideSubject().subscribe {
                slideCallback(it)
                if (expandedPeekHeight !is FitToContent && setBottomPaddingOnSlideNeeded) {
                    contentContainer?.setBottomPadding(getBottomPadding())
                }
            }

            fun getIntArgument(key: String, default: Int) =
                requireArguments().getInt(key, default).takeUnless { it == default }

            val backgroundColor = getIntArgument(CONTAINER_BACKGROUND_COLOR_ARG, Color.TRANSPARENT)
                ?: getIntArgument(CONTAINER_BACKGROUND_COLOR_RES_ARG, NIL_ID_INT)
                    ?.let(::getColorFrom)

            changeBackground(backgroundColor ?: return@apply)
        }
    }

    override fun showContent() {
        if (movablePanel?.peekHeight.isNotEqual(initHeight))
            movablePanel?.startShowingAnimation(initHeight)
    }

    override fun closeContainer() {
        if (movablePanel?.peekHeight.isEqual(hiddenPeekHeight)) requestPopBack() else hidePanel()
    }

    override fun forceClose() {
        requestPopBack()
    }

    override fun changeBackgroundFromRes(backgroundColorResId: Int) {
        changeBackground(movablePanel?.getColorFrom(backgroundColorResId) ?: return)
    }

    override fun changeBackground(backgroundColor: Int) {
        changeDrawableBackground(ColorDrawable(backgroundColor))
    }

    override fun changeDrawableBackground(background: Drawable) {
        movablePanel?.movablePanelDrawableBackground = background
    }

    override fun backPressed() {
        if (delegateBackPressToContent().not()) {
            val isForceClose = requireArguments().getBoolean(FORCE_CLOSE_ON_BACK_PRESSED, false)
            if (isForceClose) forceClose() else closeContainer()
        }
    }

    override fun requestCloseContent() {
        getNestedFragmentAs<Content>()?.onCloseContent()
    }

    private fun getBottomPadding(): Int =
        (movablePanel?.getPanelY() ?: 0) + bottomInset

    private fun onKeyboardHeightChanged(height: Int) {
        val nestedFragmentAsKeyboardEventListener = getNestedFragmentAs<KeyboardEventListener>()
        val calculateKeyboardHeightNeeded = decorFitsSystemWindows || nestedFragmentAsKeyboardEventListener != null
        val keyboardHeight =
            if (calculateKeyboardHeightNeeded) (height.takeIf { it != 0 } ?: bottomInset) - bottomInset else height
        val nestedConsumed =
            if (keyboardHeight == 0) nestedFragmentAsKeyboardEventListener?.onKeyboardCloseMeasure(keyboardHeight)
            else nestedFragmentAsKeyboardEventListener?.onKeyboardOpenMeasure(keyboardHeight)
        nestedConsumed ?: movablePanel?.contentContainer?.setBottomPadding(keyboardHeight)
    }

    private fun delegateBackPressToContent(): Boolean =
        getNestedFragmentAs<FragmentBackPress>()?.onBackPressed() ?: false

    /**
     * Скрыть панель, скрытие инициирует закрытие фрагмента
     */
    private fun hidePanel() {
        movablePanel?.peekHeight = hiddenPeekHeight
    }

    private fun isNestedFragmentReadyToClose(): Boolean =
        getNestedFragmentAs<ReadyToCloseChecker>()?.readyToClose() ?: true

    private fun requestPopBack() {
        requestCloseContent()
        popBackStack()
    }

    /**
     * Нужно ли отображать контент сразу.
     */
    private fun isInstantShowContent(): Boolean =
        requireArguments().getBoolean(INSTANT_SHOW_CONTENT_ARG, DEFAULT_INSTANT_SHOW_CONTENT_VALUE)

    private fun isOpenAnimIgnored(): Boolean = requireArguments().getBoolean(IGNORE_OPEN_ANIM)

    private fun isAutoCloseable(): Boolean = requireArguments().getBoolean(AUTO_CLOSEABLE, true)

    private fun customClosePanelAction(): CustomClosePanelAction? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requireArguments().getParcelableUniversally(CUSTOM_CLOSE_PANEL_ACTION, CustomClosePanelAction::class.java)
        else requireArguments().getParcelableUniversally(CUSTOM_CLOSE_PANEL_ACTION)
    }

    private fun addInnerFragmentIfNeeded() {
        if (innerFragmentAdded) return
        innerFragmentAdded = true
        childFragmentManager?.let { fm ->
            if (fm.findFragmentById(innerContainerId) == null) {
                getContentCreator()?.createFragment()?.let {
                    fm.beginTransaction()
                        .replace(innerContainerId, it, it::class.java.simpleName)
                        .commit()
                }
            }
        }
    }

    private inline fun <reified T> getNestedFragmentAs(): T? = try {
        childFragmentManager?.findFragmentById(innerContainerId) as? T
    } catch (e: IllegalStateException) {
        Timber.e(e)
        null
    }

    /**
     * Получить экземпляр создателя контента.
     * @return экземпляр создателя контента
     */
    private fun getContentCreator(): ContentCreatorParcelable? =
        requireArguments().getParcelableUniversally(CONTENT_CREATOR_ARG)

    private fun requireArguments(): Bundle = arguments ?: Bundle()

    abstract class AbstractBuilder<FRAGMENT : Fragment> {

        protected val bundle by lazy { Bundle() }

        /**
         * Задать режим отображения контента: мгновенно после появления
         * панели на экране или по сигналу через интерфейс [Container.Showable].
         *
         * @param instant - режим отображения контента
         */
        fun instant(instant: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(INSTANT_SHOW_CONTENT_ARG, instant)
            return this
        }

        /**
         * Задать значения высот панели. Этот метод необходимо вызывать
         * на новом экземпляре контейнера ДО попадания фрагмента в [FragmentManager].
         * Приоритетнее метода [setExpandedPeekHeight].
         *
         * @param peekHeightParams - список высот панели
         */
        fun setPeekHeightParams(peekHeightParams: List<PeekHeightParams>): AbstractBuilder<FRAGMENT> {
            bundle.putParcelableArrayList(PEEK_HEIGHT_PARAMS_ARG, peekHeightParams.asArrayList())
            return this
        }

        /**
         * Задать максимальную высоту панели. Этот метод необходимо вызывать
         * на новом экземпляре контейнера ДО попадания фрагмента в [FragmentManager].
         *
         * @param peekHeight - высота панели
         */
        fun setExpandedPeekHeight(peekHeight: MovablePanelPeekHeight): AbstractBuilder<FRAGMENT> {
            bundle.putParcelable(EXPANDED_PEEK_HEIGHT_ARG, peekHeight)
            return this
        }

        /**
         * Задать цвет фона панели. Этот метод необходимо вызывать
         * на новом экземпляре контейнера ДО попадания фрагмента в [FragmentManager].
         *
         * @param colorRes - цвет из ресурсов
         */
        fun setContainerBackgroundColorRes(@ColorRes colorRes: Int): AbstractBuilder<FRAGMENT> {
            bundle.putInt(CONTAINER_BACKGROUND_COLOR_RES_ARG, colorRes)
            return this
        }

        /**
         * Задаёт непосредственно цвет фона панели.
         * @see [setContainerBackgroundColorRes]
         */
        fun setContainerBackgroundColor(@ColorInt color: Int): AbstractBuilder<FRAGMENT> {
            bundle.putInt(CONTAINER_BACKGROUND_COLOR_ARG, color)
            return this
        }

        /**
         * Задать наличие дефолтно отступа от ручки до контента
         */
        fun setDefaultHeaderPaddingEnabled(enabled: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(DEFAULT_HEADER_PADDING_ENABLED_ARG, enabled)
            return this
        }

        /**
         * Задать тему для [MovablePanel]
         */
        fun setMovablePanelTheme(@StyleRes themeResId: Int): AbstractBuilder<FRAGMENT> {
            bundle.putInt(MOVABLE_PANEL_THEME_ARG, themeResId)
            return this
        }

        /**
         * Задать видимость тени
         */
        fun setShadowEnabled(enabled: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(SHADOW_ENABLED, enabled)
            return this
        }

        /**
         * Нужно ли игнорировать анимацию открытия, не работает с [FitToContent]
         */
        fun setOpenAnimIgnored(ignored: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(IGNORE_OPEN_ANIM, ignored)
            return this
        }

        /**
         * Закрывается ли панель автоматически при измении высоты до 0
         * Т.е. вызывается ли [popBackStack] при скрытии панели
         */
        fun setAutoCloseable(autoCloseable: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(AUTO_CLOSEABLE, autoCloseable)
            return this
        }

        /**
         * Настроить действие при закрытии шторки пользователем.
         */
        fun setCustomClosePanelAction(action: CustomClosePanelAction): AbstractBuilder<FRAGMENT> {
            bundle.putParcelable(CUSTOM_CLOSE_PANEL_ACTION, action)
            return this
        }

        /**
         * Настроить игнорирование автоблокировки скрола шторки.
         */
        fun setIgnoreLock(ignoreLock: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(IGNORE_LOCK, ignoreLock)
            return this
        }

        /**
         * Настроить ширину шторки в альбомной ориентации.
         */
        fun setPanelWidthForLandscape(panelWidth: PanelWidth): AbstractBuilder<FRAGMENT> {
            bundle.putParcelable(PANEL_WIDTH_FOR_LANDSCAPE, panelWidth)
            return this
        }

        /**
         * Задать экземпляр создателя контента. Этот метод необходимо вызывать
         * на новом экземпляре контейнера ДО попадания фрагмента в [FragmentManager].
         *
         * @param creator - создатель контента
         */
        fun setContentCreator(creator: ContentCreatorParcelable): AbstractBuilder<FRAGMENT> {
            bundle.putParcelable(CONTENT_CREATOR_ARG, creator)
            return this
        }

        /**
         * Настроить принудительное закрытие панели при onBackPressed
         */
        fun setForceCloseOnBackPressed(isForceCloseOnBackPressed: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(FORCE_CLOSE_ON_BACK_PRESSED, isForceCloseOnBackPressed)
            return this
        }

        /**
         * Установить мод работы с клавиатурой.
         * @see WindowManager.LayoutParams
         */
        fun setSoftInputMode(mode: Int): AbstractBuilder<FRAGMENT> {
            bundle.putInt(SOFT_INPUT_MODE, mode)
            return this
        }

        /**
         * Нужно ли автоматически выставлять нижний паддинг при слайде шторки
         * Не работает, если expandedPeekHeight == FitToContent
         * По умолчанию - да
         */
        fun setBottomPaddingOnSlideNeeded(set: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(SET_BOTTOM_PADDING, set)
            return this
        }

        /**
         * Нужно ли автоматически закрыть шторку при клике на область тени
         */
        fun setCloseOnShadowClick(close: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(CLOSE_ON_SHADOW_CLICK, close)
            return this
        }

        /**
         * Нужно ли скрывать клавиатуру при запуске
         * По умолчанию - да
         */
        fun hideKeyboardOnStart(hide: Boolean): AbstractBuilder<FRAGMENT> {
            bundle.putBoolean(HIDE_KEYBORAD_ON_START, hide)
            return this
        }

        /**
         * Функция для создания фрагмента
         */
        abstract fun build(): FRAGMENT
    }

    /**
     * Модель параметров для инициализации компонента шторки
     *
     * @param type тип высоты
     * @param peekHeight  значение высоты
     */
    @Parcelize
    data class PeekHeightParams(
        val type: PeekHeightType,
        val peekHeight: MovablePanelPeekHeight
    ) : Parcelable

    /**
     * Типы высот
     */
    @Parcelize
    enum class PeekHeightType : Parcelable {
        /**
         * Скрытое состояние
         */
        HIDDEN,

        /**
         * Состояние для инициализации
         */
        INIT,

        /**
         * Максимальное состояние
         */
        EXPANDED,

        /**
         * Неимеющие значение для обертки, но нужно для шторки
         */
        DEFAULT
    }
}

/**
 * Интерфейс для взаимодействия с панелью
 *
 * @author ga.malinskiy
 */
interface Lockable {

    /**
     * Принудительная блокировка панели
     */
    fun lockPanel(locked: Boolean = true)
}

/**
 * Интерфейс для взаимодействия с панелью
 *
 * @author ga.malinskiy
 */
interface ForceCloseable {

    /**
     * Принудительное закрытие панели
     */
    fun forceClose()
}

/**
 * Интерфейс для проверки готовности контент экрана к закрытию
 *
 * @author ga.malinskiy
 */
interface ReadyToCloseChecker {

    /**
     * Готов закрыться?
     */
    fun readyToClose(): Boolean
}

/**
 * Интерфейс для динамического изменения фона панели
 *
 * @author ga.malinskiy
 */
interface BackgroundChangeable {

    /** @SelfDocumented */
    fun changeBackgroundFromRes(@ColorRes backgroundColorResId: Int)

    /** @SelfDocumented */
    fun changeBackground(@ColorInt backgroundColor: Int)

    /** @SelfDocumented */
    fun changeDrawableBackground(background: Drawable)
}

/**
 * Действие по закрытию шторки пользователем.
 *
 * @author mb.kruglova
 */
@Parcelize
class CustomClosePanelAction(val action: () -> Unit) : Parcelable