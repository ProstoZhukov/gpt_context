package ru.tensor.sbis.design.toolbar.appbar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.appbar.background.AspectRatioChangeListener
import ru.tensor.sbis.design.toolbar.appbar.background.resolveBackgroundStrategy
import ru.tensor.sbis.design.toolbar.appbar.behavior.SbisAppBarLayoutBehavior
import ru.tensor.sbis.design.toolbar.appbar.color.ColorUpdateFunction
import ru.tensor.sbis.design.toolbar.appbar.color.getColorUpdateFunction
import ru.tensor.sbis.design.toolbar.appbar.gradient.FixedGradientWithFillHelper
import ru.tensor.sbis.design.toolbar.appbar.gradient.GradientHelper
import ru.tensor.sbis.design.toolbar.appbar.gradient.SimpleGradientHelper
import ru.tensor.sbis.design.toolbar.appbar.model.AppBarModel
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel
import ru.tensor.sbis.design.toolbar.appbar.offset.NormalOffsetChangeListener
import ru.tensor.sbis.design.toolbar.appbar.offset.NormalOffsetObserver
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.utils.extentions.getActivity
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.utils.getIdSet

private const val DEFAULT_OFFSET = 1f
internal const val TAG_GRADIENT = "GRADIENT_VIEW"

/**
 * Расширение [AppBarLayout] - [графическая шапка](http://axure.tensor.ru/MobileStandart8/#p=%D0%B3%D1%80%D0%B0%D1%84%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D1%88%D0%B0%D0%BF%D0%BA%D0%B0&g=1).
 * Реализация выступает в роли медиатора для обеспечения стандартного поведения вложенных компонентов.
 *
 * Дочерние [View], которые реализуют интерфейс [NormalOffsetObserver] будут автоматически подписаны на изменения
 * состояния развёртывания (добавлять через [addOffsetObserver] не нужно)
 *
 * @author ma.kolpakov
 * Создан 9/19/2019
 */
class SbisAppBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.SbisAppBarLayoutTheme
) : AppBarLayout(
    ContextThemeWrapper(context, context.getDataFromAttrOrNull(defStyleAttr) ?: R.style.SbisAppBar),
    attrs
) {

    /**
     * Идентификатор [View] с toolbar-ом и изображением
     */
    @LayoutRes
    private val layoutId: Int

    /**
     * Идентификатор целевой [View] для установки фона
     */
    @IdRes
    private val backgroundViewId: Int

    /**
     * Идентификатор целевой [View] для задания градиента
     */
    @IdRes
    private val gradientHolderViewId: Int

    @IdRes
    private val titleViewId: Int

    private val colorDependentViewId: Set<Int>

    private val offsetObserversList = mutableListOf<NormalOffsetObserver>()

    private val backgroundStrategy by lazy { resolveBackgroundStrategy(backgroundView, aspectRatioChangeListener) }

    private val gradient: GradientHelper? by lazy {
        gradientHolderView?.let {
            if (isTitleWithBackground) FixedGradientWithFillHelper(it) else SimpleGradientHelper(it)
        }
    }

    private val collapseUpdateHelper: CollapseUpdateHelper by lazy {
        CollapseUpdateHelper(this, collapsingToolbar, backgroundView, titleView)
    }

    private val collapsingToolbar: CollapsingToolbarLayout by lazy {
        checkNotNull(findCollapsingToolbarLayout()) {
            "Unable to find direct child CollapsingToolbarLayout"
        }
    }

    private lateinit var aspectRatioChangeListener: AspectRatioChangeListener

    private val isTitleWithBackground: Boolean

    private val defaultStateListAnimator = stateListAnimator

    /**
     * Модель состояния графической шапки. Установка модели запускает процедуру обновления внешнего вида.
     * Устанавливать модель можно только после завершения процедуры _inflate_
     */
    var model: AppBarModel = AppBarModel()
        set(value) {
            refreshView(field, value)
            field = value
        }

    @ColorInt
    private val defaultStatusBarColor: Int

    private var isAnyOffsetApplied = false

    internal var lastInsets: WindowInsets? = null
        private set

    init {
        @Suppress("NAME_SHADOWING") val context = getContext()

        with(
            context.theme.obtainStyledAttributes(
                attrs, R.styleable.SbisAppBarLayout, R.attr.appBarStyle, R.style.SbisAppBar
            )
        ) {

            layoutId = getResourceId(R.styleable.SbisAppBarLayout_SbisAppBarLayout_layoutId, View.NO_ID)

            // TODO: Переделать на стратегии https://online.sbis.ru/doc/edfc4019-0c3c-4cc3-8484-edc9d63491fd
            if (layoutId != View.NO_ID) {
                LayoutInflater.from(context).inflate(layoutId, this@SbisAppBarLayout, true)

                backgroundViewId = R.id.toolbar_mainImage

                fitsSystemWindows = true

                colorDependentViewId = setOf(R.id.toolbar_collapsingToolbar, R.id.toolbar_sbisToolbar)
                gradientHolderViewId = R.id.toolbar_gradient_view

                titleViewId = if (findViewById<View>(R.id.toolbar_center_text) != null) {
                    R.id.toolbar_titleView
                } else {
                    View.NO_ID
                }

                check(!hasValue(R.styleable.SbisAppBarLayout_SbisAppBarLayout_image)) {
                    "If attribute SbisAppBarLayout_layoutId is set then not needed SbisAppBarLayout_image"
                }

                check(!hasValue(R.styleable.SbisAppBarLayout_SbisAppBarLayout_gradient_holder)) {
                    "If attribute SbisAppBarLayout_layoutId is set then not needed SbisAppBarLayout_gradient_holder"
                }

                check(!hasValue(R.styleable.SbisAppBarLayout_SbisAppBarLayout_color_dependent_views)) {
                    "If attribute SbisAppBarLayout_layoutId is set " +
                        "then not needed SbisAppBarLayout_color_dependent_views"
                }
            } else {
                backgroundViewId = getResourceId(R.styleable.SbisAppBarLayout_SbisAppBarLayout_image, View.NO_ID)

                gradientHolderViewId =
                    getResourceId(R.styleable.SbisAppBarLayout_SbisAppBarLayout_gradient_holder, View.NO_ID)

                colorDependentViewId = getIdSet(
                    context, R.styleable.SbisAppBarLayout_SbisAppBarLayout_color_dependent_views
                ) ?: emptySet()

                titleViewId = getResourceId(R.styleable.SbisAppBarLayout_SbisAppBarLayout_titleView, View.NO_ID)
            }

            isTitleWithBackground =
                getBoolean(R.styleable.SbisAppBarLayout_SbisAppBarLayout_titleWithBackground, false)

            recycle()
        }

        offsetObserversList.add(object : NormalOffsetObserver {
            override fun onOffsetChanged(position: Float) {
                // обновление значения раскрытия в актуальной модели
                model.currentOffset = position
                gradient?.update(position)
                collapseUpdateHelper.updateOffset(position)
            }
        })
        addOnOffsetChangedListener(NormalOffsetChangeListener(offsetObserversList, ::getTotalScrollRange))

        defaultStatusBarColor = StatusBarHelper.getStatusBarColor(getActivity())

        setOnApplyWindowInsetsListener { v, insets ->
            val newInsets = insets.takeIf { ViewCompat.getFitsSystemWindows(this) }
            lastInsets = newInsets
            insets
        }
    }

    /**
     * Целевая [View] для установки фона. Определена после блока инициализации из-за требования языка обеспечить
     * инициализацию [backgroundViewId]
     */
    private val backgroundView by lazy {
        val view = findViewById<View>(backgroundViewId)
        check(backgroundViewId == View.NO_ID || view != null) {
            "Unable get child view with id $backgroundViewId"
        }
        // если целевая View не определена, будем пытаться установить фон "в себя"
        view ?: this
    }

    private val gradientHolderView: View? by lazy {
        val view = findViewById<View>(gradientHolderViewId)
        check(gradientHolderViewId == View.NO_ID || view != null) {
            "Unable get child view with id $gradientHolderViewId"
        }
        view?.apply { tag = TAG_GRADIENT }
    }

    private val titleView: SbisTitleView? by lazy {
        val view = findViewById<SbisTitleView>(titleViewId)
        check(titleViewId == View.NO_ID || view != null) {
            "Unable get child view with id $titleViewId"
        }
        view
    }

    /**
     * Набор зависымых от цвета элементов графической шапки
     * - ключ - [View], чей цвет нужно обновлять
     * - значение - функция обновения цвета [ColorUpdateFunction]
     */
    private val colorModelObservers: Map<View, ColorUpdateFunction<View>> by lazy {
        // получим необходимые View
        val idSet: Set<View> = colorDependentViewId.mapTo(hashSetOf(), ::findViewById)
        // совместим их с функциями обновления цвета
        idSet.zip(idSet.map(::getColorUpdateFunction)).toMap()
    }

    /**
     * Добавление подписки на раскрытие графической шапки
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun addOffsetObserver(observer: NormalOffsetObserver) {
        offsetObserversList.add(observer)
    }

    /**
     * Удаление подписки на раскрытие графической шапки
     *
     * @return результат работы [MutableList.remove]
     */
    @Suppress("unused")
    fun removeOffsetObserver(observer: NormalOffsetObserver) = offsetObserversList.remove(observer)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isTitleWithBackground) {
            collapsingToolbar.setTitleWithBackground()
        }
        aspectRatioChangeListener = AspectRatioChangeListener(this, backgroundView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (gradientHolderView?.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.BOTTOM

        setStatusBarColor(Color.TRANSPARENT)

        gradient?.let(collapsingToolbar::setOnTitleLineCountChangeListener)

        aspectRatioChangeListener.invokeWithCurrentAspectRatio()
    }

    override fun onDetachedFromWindow() {
        setStatusBarColor(defaultStatusBarColor)
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = if (gradient != null && isTitleWithBackground) {
            if (!isLaidOut) {
                // В этом случае высота заголовка ещё неизвестна, но требуется сразу определить полную высоту шапки,
                // чтобы избежать видимого изменения размера
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                // TODO: https://online.sbis.ru/opendoc.html?guid=0c26655b-d282-4337-b150-1dd2dbb1e828
                collapsingToolbar.layout(0, 0, measuredWidth, measuredHeight)
            }
            val baseHeight = MeasureSpec.getSize(heightMeasureSpec)
            val additionalHeight = collapsingToolbar.fullTitleHeight
            gradient!!.setFillHeight(additionalHeight)
            MeasureSpec.makeMeasureSpec(baseHeight + additionalHeight, MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.let { aspectRatioChangeListener.isEnabled = it.height <= 0 }

        super.setLayoutParams(params)

        aspectRatioChangeListener.invokeWithCurrentAspectRatio()
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<AppBarLayout> = SbisAppBarLayoutBehavior()

    /**
     * Задание возможности сворачивания шапки. По умолчанию определяется флагами прокрутки [CollapsingToolbarLayout]
     */
    internal fun setCollapsible(isCollapsible: Boolean) {
        val newScrollFlags = if (isCollapsible) getDefaultScrollFlags() else 0
        with(collapsingToolbar) {
            val params = layoutParams as LayoutParams
            if (params.scrollFlags != newScrollFlags) {
                layoutParams = params.apply { scrollFlags = newScrollFlags }
            }
        }
        if (!isCollapsible && stateListAnimator != null) {
            // избавляемся от отображения тени под шапкой, если она не сворачивается
            stateListAnimator = null
        } else if (isCollapsible && stateListAnimator == null) {
            stateListAnimator = defaultStateListAnimator
        }
    }

    /**
     * Устанавливает цвет статус бара
     */
    private fun setStatusBarColor(@ColorInt color: Int) {
        getActivity()
            .takeUnless { StatusBarHelper.getStatusBarColor(it) == color }
            ?.let {
                val darkText = model.color?.darkText == true && model.color?.canChangeStatusBarLightMode == true
                StatusBarHelper.setStatusBarColor(it, color, darkText)
            }
    }

    private fun updateStatusBarIconColor(isDarkText: Boolean) {
        if (isDarkText) {
            StatusBarHelper.setLightMode(getActivity())
        } else {
            StatusBarHelper.setDarkMode(getActivity())
        }
    }

    /**
     * Метод обновления. Обновление только тех частей UI, модели для которых изменились
     */
    private fun refreshView(current: AppBarModel, new: AppBarModel) {
        new.background.takeIf { it != current.background }?.let(backgroundStrategy::setModel)
        new.color.takeIf { it != current.color }?.let(::onColorModelChanged)

        new.currentOffset.takeIf { (!it.isNaN() || !isAnyOffsetApplied) && it != current.currentOffset }
            ?.let { updateOffset(it) }

        if (new.color != current.color || new.content != current.content) {
            collapseUpdateHelper.updateModel(new)
        }
    }

    /**
     * Доставка обновлённой цветовой модели
     */
    private fun onColorModelChanged(model: ColorModel) {
        for ((view, function) in colorModelObservers) {
            function.updateColorModel(view, model)
        }
        gradient?.updateModel(model)

        if (model.canChangeStatusBarLightMode) updateStatusBarIconColor(model.darkText)
    }

    private fun updateOffset(normalOffset: Float) {
        val behavior = (layoutParams as? CoordinatorLayout.LayoutParams)
            ?.behavior as Behavior?
            ?: return
        val offset = normalOffset.takeUnless { it.isNaN() }
            ?: DEFAULT_OFFSET
        // установка раскрытия через поведение
        behavior.topAndBottomOffset = (totalScrollRange * (offset - 1)).toInt()
        isAnyOffsetApplied = true
    }

    private fun findCollapsingToolbarLayout(): CollapsingToolbarLayout? {
        return (0 until childCount)
            .map(::getChildAt)
            .filterIsInstance<CollapsingToolbarLayout>()
            .firstOrNull()
    }

    private fun getDefaultScrollFlags() =
        LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
}