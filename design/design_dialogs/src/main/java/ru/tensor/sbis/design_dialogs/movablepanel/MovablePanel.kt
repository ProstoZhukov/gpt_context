package ru.tensor.sbis.design_dialogs.movablepanel

import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import android.widget.RelativeLayout.ALIGN_PARENT_TOP
import android.widget.RelativeLayout.CENTER_HORIZONTAL
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.VisibleForTesting
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design.utils.AnimationUtil
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.extentions.getDrawableFrom
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.FitToContent
import kotlin.math.abs
import ru.tensor.sbis.design.R as RDesign

//region Константы высоты панели по оси Z
const val GRIP_VIEW_TRANSLATION_Z = 18F
const val GRIP_VIEW_TOP_DOWN_TRANSLATION_Z = 6F
const val GRIP_VIEW_WITHOUT_TRANSLATION_Z = 0F
//endregion

/**
 * Компонент шторка, основанный на Behavior
 * http://axure.tensor.ru/MobileStandart8/#g=1&p=%D1%88%D1%82%D0%BE%D1%80%D0%BA%D0%B0
 *
 * @author ga.malinskiy
 */
class MovablePanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.MovablePanelTheme,
    defStyleRes: Int = R.style.MovablePanelDefaultTheme
) : FrameLayout(
    ThemeContextBuilder(
        context,
        defStyleAttr = defStyleAttr,
        defaultStyle = defStyleRes
    ).build(),
    attrs,
    defStyleAttr
) {

    /**
     * Получение актуального отображения для корректной работы скрола LockableBehavior
     */
    var provideViewPagerCurrentView: () -> View? = { null }

    /**
     * При конфликте скроллящихся вью на экране, вручную поставить необходимый.
     */
    var provideCurrentScrollView: () -> View? = { null }

    /**
     * Установка и получение актуальной высоты панели
     */
    var peekHeight: MovablePanelPeekHeight?
        get() = behavior?.getPeekHeight()
        set(value) {
            value?.let { behavior?.setPeekHeight(it) }
        }

    /**
     * Заблокированы ли движения панели (принудительно пользователем компонента)
     */
    var isBehaviorLocked: Boolean
        get() = behavior?.isBehaviorLocked ?: false
        set(value) {
            behavior?.isBehaviorLocked = value
        }

    /**
     * Игнорирование автоблокировки скрола шторки
     */
    var ignoreLock: Boolean
        get() = behavior?.ignoreLock ?: false
        set(value) {
            behavior?.ignoreLock = value
        }

    /**
     * Игнорировать анимацию подъема шторки, не работает с [FitToContent]
     */
    var ignoreOpenAnim: Boolean
        get() = behavior?.ignoreOpenAnim ?: false
        set(value) {
            behavior?.ignoreOpenAnim = value
        }

    /**
     * Анимировать изменение родительского контейнера.
     */
    var animateParentHeightChanges: Boolean
        get() = behavior?.animateParentHeightChanges ?: true
        set(value) {
            behavior?.animateParentHeightChanges = value
        }

    /**
     * Цвет заливки всей панели (настраиваемый)
     */
    @ColorInt
    var movablePanelBackground: Int = getColorFrom(RDesign.color.palette_color_white1)
        set(value) {
            field = value
            movablePanelDrawableBackground = ColorDrawable(value)
        }

    /**
     * Цвет заливки всей панели (настраиваемый)
     */
    var movablePanelDrawableBackground: Drawable = ColorDrawable(movablePanelBackground)
        set(value) {
            field = value
            singleContentBackgroundContainer?.background = value
        }

    /**
     * Факт наличия дефолтного отступа между контроллером и контентом
     */
    var defaultHeaderPaddingEnabled: Boolean = true
        set(value) {
            field = value
            updateContentContainerOffsets()
        }

    /**
     * Контейнер для отображения контента
     */
    var contentContainer: FrameLayout? = null

    /**
     * Рутовый контейнер для контейнера [contentContainer]
     */
    var contentRootContainer: LinearLayout? = null

    /**
     * Рутовый контейнер для всей вью шторки
     */
    var movablePanelContainer: CoordinatorLayout? = null

    /**
     * Ширина шторки.
     */
    var panelWidth: PanelWidth = PanelWidth.MATCH_PARENT
        set(value) {
            field = value
            updateMovablePanelContainerWidth()
        }

    /**
     * Контейнер для установки [movablePanelBackground], [movablePanelDrawableBackground]
     */
    private var singleContentBackgroundContainer: RelativeLayout? = null

    /**
     * Контейнер фона
     */
    private var movablePanelShadow: FrameLayout? = null

    /**
     * Вью для создания невидимого отступа, равного размеру верхнего инсета
     * (необходимо для использования с прозрачным StatusBar)
     */
    private var insetView: View? = null

    /**
     * Вью "ручки" для движения шторки
     */
    private var gripView: View? = null

    @VisibleForTesting
    internal var behavior: LockableBehavior? = null

    private val panelStateSubject: PublishSubject<MovablePanelPeekHeight> = PublishSubject.create()
    private val panelSlideSubject: PublishSubject<Float> = PublishSubject.create()

    private val topDownDirection: Boolean
    private val contentContainerId: Int
    private var shadowEnabled: Boolean
    private var initShadowOffset = 0F
    private val topOffset: Int

    @ColorInt
    private val shadowColor: Int

    @ColorInt
    private val gripColor: Int
    private val gripShadowVisible: Boolean

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.MovablePanelStyle, defStyleAttr, 0)) {

            val backgroundResourceId = getResourceId(R.styleable.MovablePanelStyle_MovablePanel_background, -1)
            if (backgroundResourceId != -1) {
                getDrawableFrom(backgroundResourceId)?.let { movablePanelDrawableBackground = it }
            } else {
                movablePanelBackground = getColor(
                    R.styleable.MovablePanelStyle_MovablePanel_background,
                    getColorFrom(RDesign.color.palette_color_white1)
                )
            }

            topDownDirection = getBoolean(R.styleable.MovablePanelStyle_MovablePanel_topDownDirection, false)
            contentContainerId =
                getResourceId(
                    R.styleable.MovablePanelStyle_MovablePanel_contentContainerId,
                    R.id.movable_panel_content_container_id
                )
            shadowEnabled = getBoolean(R.styleable.MovablePanelStyle_MovablePanel_shadowBackgroundEnabled, false)
            shadowColor = getColor(
                R.styleable.MovablePanelStyle_MovablePanel_shadowBackgroundColor,
                getColorFrom(R.color.movable_panel_shadow_background)
            )
            gripColor = getColor(
                R.styleable.MovablePanelStyle_MovablePanel_gripColor,
                getColorFromAttr(RDesign.attr.translucentBackgroundColor)
            )
            defaultHeaderPaddingEnabled =
                getBoolean(R.styleable.MovablePanelStyle_MovablePanel_defaultHeaderPaddingEnabled, true)
            gripShadowVisible = getBoolean(R.styleable.MovablePanelStyle_MovablePanel_gripShadowVisible, true)

            topOffset = getDimensionPixelSize(R.styleable.MovablePanelStyle_MovablePanel_topOffset, 0)

            panelWidth = getInteger(
                R.styleable.MovablePanelStyle_MovablePanel_panelWidth,
                PanelWidth.MATCH_PARENT.ordinal
            ).toPanelWidth()

            recycle()
        }

        initViewConstructor(context, attrs)

        val rootContainerParams = contentRootContainer?.layoutParams as? CoordinatorLayout.LayoutParams
        val rootContainerBehavior = rootContainerParams?.behavior as? LockableBehavior

        behavior = rootContainerBehavior?.apply {

            setMovingCallback(object : MovablePanelMovingCallback {

                private var shadowVisible = false
                private var lastState: MovablePanelPeekHeight? = null

                override fun onHeightChanged(view: View, state: MovablePanelPeekHeight) {
                    if (lastState.isNotEqual(state)) {
                        lastState = state
                        panelStateSubject.onNext(state)
                    }
                }

                override fun onSlide(view: View, slideOffset: Float) {
                    panelSlideSubject.onNext(slideOffset)

                    val activeShadow = shadowEnabled && slideOffset > initShadowOffset
                    val changed = shadowVisible != activeShadow
                    shadowVisible = activeShadow
                    if (changed) {
                        listOfNotNull(movablePanelShadow).forEach {
                            it.isClickable = activeShadow
                            val from = if (activeShadow) 0F else 1F
                            val to = if (activeShadow) 1F else 0F
                            AnimationUtil.updateAlpha(it, from, to, null)
                        }
                    }
                }

                override fun getPagerCurrentView(): View? = provideViewPagerCurrentView()

                override fun getScrollableView(): View? = provideCurrentScrollView()
            })
        }
    }

    /**
     * Нужно ли показывать затемнение за шторкой
     */
    fun setShadowEnabled(enabled: Boolean) {
        shadowEnabled = enabled
    }

    /**
     * Установка обработчика кликов по затемненной области за шторкой
     *
     * @param actionOnClick - действие, которое нужно выполнить, при клике на затемненную область
     */
    fun setOnShadowClickListener(actionOnClick: () -> Unit) {
        movablePanelShadow?.setOnClickListener { actionOnClick() }
        movablePanelShadow?.isClickable = false
    }

    /**
     * Установить значение peekHeight после которого будет показано затемнение за шторкой
     */
    fun setInitShadowPeekHeight(peekHeight: MovablePanelPeekHeight) {
        setGlobalListener { calculateSlideOffsetByPeekHeight(peekHeight)?.let { initShadowOffset = it } }
    }

    /**
     * Рассчитать значение slideOffset относительно переданной высоты [peekHeight]
     */
    fun calculateSlideOffsetByPeekHeight(peekHeight: MovablePanelPeekHeight): Float? =
        behavior?.calculateSlideOffsetByPeekHeight(peekHeight, resources)

    /**
     * Установка возможных высот панели и инициализирующее значение (вызывается при инициализации)
     *
     * @throws IllegalArgumentException - если передать менььше 2х и больше 4х значений высоты
     */
    fun setPeekHeightList(peekHeightList: List<MovablePanelPeekHeight>, initPeekHeight: MovablePanelPeekHeight) {
        behavior?.setPeekHeightList(peekHeightList, initPeekHeight)
        if (peekHeightList.any { it is FitToContent }) {
            contentContainer?.updateLayoutParams<RelativeLayout.LayoutParams> {
                height = WRAP_CONTENT
                addRule(if (topDownDirection) ALIGN_PARENT_BOTTOM else ALIGN_PARENT_TOP)
            }
        }
    }

    /**
     * Начать анимацию показа для высоты [peekHeight].
     */
    fun startShowingAnimation(peekHeight: MovablePanelPeekHeight) {
        behavior?.startShowingAnimation(peekHeight)
    }

    /**
     * Метод получения отступа для контента
     */
    fun getContentPadding(): Int = getPanelY() + getHeaderHeight()

    /**
     * Получить значение Y панели
     */
    fun getPanelY(): Int = abs(contentRootContainer?.y?.toInt() ?: 0)

    /**
     * Получить значение высоты панели
     */
    fun getPanelHeight(): Int = contentRootContainer?.height ?: 0

    /**
     * Получить значение высоты шапки панели
     */
    fun getHeaderHeight(): Int {
        val gripViewHeight =
            if (defaultHeaderPaddingEnabled) gripView?.run { height + marginTop + marginBottom } ?: 0 else 0
        val insetViewHeight = insetView?.height ?: 0
        return gripViewHeight + insetViewHeight
    }

    /**
     *  Получить объект для подписки на поток событий об изменениях состояния панели при ее движениях
     */
    fun getPanelStateSubject(): Observable<MovablePanelPeekHeight> = panelStateSubject

    /**
     *  Получить объект для подписки на поток событий об оффсете панели при ее движениях
     */
    fun getPanelSlideSubject(): Observable<Float> = panelSlideSubject

    /**
     * Обновить цвет контроллера
     */
    fun updateGripViewColor(@ColorInt color: Int) {
        gripView?.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            val radius = getDimenFrom(R.dimen.movable_panel_grip_radius).toFloat()
            cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            setColor(color)
        }
    }

    /**
     * Обновить видимость контроллера
     */
    fun updateGripViewVisibility(visibility: Int) {
        gripView?.visibility = visibility
    }

    /**
     * Обновить translationZ шторки
     */
    @Deprecated("Метод устарел, нужно удалить использование")
    fun updateContainerTranslationZ(value: Float) {
        (contentContainer?.parent as? RelativeLayout)?.translationZ = value
    }

    private fun initViewConstructor(context: Context, attrs: AttributeSet?) {
        movablePanelShadow = FrameLayout(context, attrs).apply {
            id = R.id.design_dialogs_movable_panel_shadow_id
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(shadowColor)
            alpha = 0F
        }

        movablePanelContainer = CoordinatorLayout(context, attrs).apply {
            id = R.id.design_dialogs_movable_panel_coordinador_id
            val isMatchParent = panelWidth == PanelWidth.MATCH_PARENT
            val width = if (isMatchParent) MATCH_PARENT else resources.displayMetrics.widthPixels / 2
            layoutParams = LayoutParams(width, MATCH_PARENT).also {
                it.updateMovablePanelContainer(isMatchParent)
            }
        }
        createInsetView(context, attrs)
        createGripView(context, attrs)
        createContentContainer(context, attrs)

        addView(movablePanelShadow)
        addView(movablePanelContainer)
    }

    private fun createInsetView(context: Context, attrs: AttributeSet?) {
        insetView = View(context, attrs).apply {
            id = R.id.design_dialogs_movable_panel_inset_id
            // высота необходимая для отображения тени при использовании elevation
            val height = getDimenFrom(R.dimen.movable_panel_inset_view_default_height)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, height)
        }
    }

    private fun createGripView(context: Context, attrs: AttributeSet?) {
        gripView = View(context, attrs).apply {
            id = R.id.design_dialogs_movable_panel_grip_id
            layoutParams = RelativeLayout.LayoutParams(
                getDimenFrom(R.dimen.movable_panel_grip_width),
                getDimenFrom(R.dimen.movable_panel_grip_height)
            ).also {
                it.addRule(if (topDownDirection) ALIGN_PARENT_BOTTOM else ALIGN_PARENT_TOP)
                it.addRule(CENTER_HORIZONTAL)
                val topPadding = getDimenFrom(
                    if (topDownDirection) R.dimen.movable_panel_layout_offset_8
                    else R.dimen.movable_panel_layout_offset_6
                )
                val bottomPadding = getDimenFrom(
                    if (topDownDirection) R.dimen.movable_panel_layout_offset_6
                    else R.dimen.movable_panel_layout_offset_8
                )
                it.setMargins(0, topPadding, 0, bottomPadding)
            }
        }
        updateGripViewColor(gripColor)
    }

    private fun createContentContainer(context: Context, attrs: AttributeSet?) {
        contentRootContainer = LinearLayout(context, attrs).apply {
            id = R.id.design_dialogs_movable_panel_content_root_container_id
            layoutParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                behavior = LockableBehaviorImpl(context, attrs)
            }
            orientation = VERTICAL
            isClickable = true
            if (topDownDirection) setVerticalPadding(topOffset) else setVerticalPadding(bottom = topOffset)
        }

        contentContainer = FrameLayout(context, attrs).apply {
            id = contentContainerId
            layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            clipToPadding = false
        }
        updateContentContainerOffsets()

        singleContentBackgroundContainer = RelativeLayout(context, attrs).apply {
            id = R.id.design_dialogs_movable_panel_single_content_background_container_id
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0, 1F)
            val typeOfTranslationZ = if (topDownDirection) GRIP_VIEW_TOP_DOWN_TRANSLATION_Z else GRIP_VIEW_TRANSLATION_Z
            translationZ = if (gripShadowVisible) typeOfTranslationZ else GRIP_VIEW_WITHOUT_TRANSLATION_Z
            background = movablePanelDrawableBackground
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = getDimenFrom(R.dimen.movable_panel_radius_14)
                    val top = if (topDownDirection) -radius else 0
                    val bottom = if (topDownDirection) view.height else view.height + radius
                    outline.setRoundRect(0, top, view.width, bottom, radius.toFloat())
                }
            }
            clipToOutline = true
            addView(contentContainer)
            addView(gripView)
        }

        contentRootContainer?.apply {
            if (topDownDirection) {
                addView(singleContentBackgroundContainer)
                addView(insetView)
            } else {
                addView(insetView)
                addView(singleContentBackgroundContainer)
            }
        }
        movablePanelContainer?.addView(contentRootContainer)
    }

    private fun updateContentContainerOffsets() {
        val offset = getDimenFrom(R.dimen.movable_panel_default_header_height)
        val defaultOffset = if (defaultHeaderPaddingEnabled) offset else 0
        val topOffset = if (topDownDirection) 0 else defaultOffset
        val bottomOffset = if (topDownDirection) defaultOffset else 0
        contentContainer?.setVerticalPadding(topOffset, bottomOffset)
    }

    /** Обновить ширину для movablePanelContainer. */
    private fun updateMovablePanelContainerWidth() {
        movablePanelContainer?.updateLayoutParams<LayoutParams> {
            val isMatchParent = panelWidth == PanelWidth.MATCH_PARENT
            width = if (isMatchParent) MATCH_PARENT else resources.displayMetrics.widthPixels / 2
            updateMovablePanelContainer(isMatchParent)
        }
    }

    /** Обновить LayoutParams для movablePanelContainer. */
    private fun LayoutParams.updateMovablePanelContainer(isMatchParent: Boolean) {
        gravity = when (panelWidth) {
            PanelWidth.START_HALF -> Gravity.START
            PanelWidth.CENTER_HALF -> Gravity.CENTER_HORIZONTAL
            PanelWidth.END_HALF -> Gravity.END
            else -> Gravity.FILL_HORIZONTAL
        }

        if (isMatchParent.not()) {
            val margin = getDimenFrom(R.dimen.movable_panel_layout_offset_12)

            marginStart = margin
            marginEnd = margin
        }
    }

    private fun setGlobalListener(action: () -> Unit) {
        var listener: OnLayoutChangeListener? = null

        listener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (height > 0 && contentRootContainer != null && getPanelHeight() > 0) {
                action()
                removeOnLayoutChangeListener(listener)
            }
        }

        addOnLayoutChangeListener(listener)
    }
}

/**
 * Расширение для получения Dimen из ресурсов с помощью resources
 */
internal fun View.getDimenFrom(@DimenRes dimenResId: Int): Int = resources.getDimensionPixelSize(dimenResId)

/**@SelfDocumented*/
internal fun View.setVerticalPadding(top: Int = 0, bottom: Int = 0) {
    setPadding(paddingLeft, top, paddingRight, bottom)
}

@Parcelize
enum class PanelWidth : Parcelable {
    MATCH_PARENT,
    START_HALF,
    CENTER_HALF,
    END_HALF
}

private fun Int.toPanelWidth(): PanelWidth =
    when (this) {
        1 -> PanelWidth.START_HALF
        2 -> PanelWidth.CENTER_HALF
        3 -> PanelWidth.END_HALF
        else -> PanelWidth.MATCH_PARENT
    }
