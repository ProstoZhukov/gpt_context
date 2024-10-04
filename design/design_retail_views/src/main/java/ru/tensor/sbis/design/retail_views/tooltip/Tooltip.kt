package ru.tensor.sbis.design.retail_views.tooltip

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.view.doOnDetach
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.theme.Position
import ru.tensor.sbis.design.utils.extentions.getActivity

/**
 * Класс для создания вьюхи-попапа TooltipView с треугольным указателем на якорную вьюху. Пока можно настраивать только текст.
 * При показе также добавляется прозрачная вьюха на весь экран, которая ловит все нажатия, скрывает тултип и пробрасывает нажатия дальше.
 *
 * Использование может быть следующим:
 * Tooltip
 *  .on(this, infoButton)
 *  .text(resources.getString(R.string.catalog_settings_hint))
 *  .setPosition(Position.BOTTOM)
 *  .show()

 */
@Suppress("RedundantSuppression")
class Tooltip private constructor(
    private var rootView: ViewGroup,
    activity: Activity,
    private val view: View,
    private val focusControlEnabled: Boolean,
    private var showInSbisContainer: Boolean = false
) {
    private val helperClickableView: View = View(activity)
    private val tooltipView: TooltipView = TooltipView(activity, helperClickableView)
    private var isShowHelperView = true
    private var scrollBehavior = ScrollBehavior.MOVE

    init {
        if (focusControlEnabled) {
            view.isFocusableInTouchMode = true
            view.requestFocus()
        }
    }

    companion object {
        // Для отрисовки тултипов на диалогах, отображаемых внутри SbisContainer.
        // https://online.sbis.ru/opendoc.html?guid=1b7ef700-650f-4c16-bd1a-06cd95c8e40b&client=3
        private const val TRANSLATION_Z = 100f

        fun on(activity: Activity, view: View, focusControlEnabled: Boolean = true): Tooltip =
            Tooltip(activity.window.decorView as ViewGroup, activity, view, focusControlEnabled)

        fun on(fragment: Fragment, view: View, focusControlEnabled: Boolean = true): Tooltip {
            val isWrapped = fragment.parentFragment is DialogFragment
            val rootView = if (isWrapped) {
                fragment.requireParentFragment().requireView()
            } else {
                fragment.requireActivity().window.decorView
            }
            return Tooltip(
                rootView as ViewGroup,
                fragment.requireActivity(),
                view,
                focusControlEnabled,
                isWrapped
            )
        }

        /**
         * Показ тултипа для фрагмента, отображаемого внутри SbisContainer.
         */
        fun onContainerContentFragment(fragment: Fragment, view: View, focusControlEnabled: Boolean = true): Tooltip =
            Tooltip(
                fragment.requireParentFragment().requireView() as ViewGroup,
                fragment.requireActivity(),
                view,
                focusControlEnabled,
                true
            )

        fun on(dialogFragment: DialogFragment, view: View, focusControlEnabled: Boolean = true): Tooltip =
            Tooltip(
                dialogFragment.dialog?.window!!.decorView as ViewGroup,
                dialogFragment.requireActivity(),
                view,
                focusControlEnabled
            )

        fun on(anchorView: View, focusControlEnabled: Boolean = true): Tooltip =
            Tooltip(
                rootView = anchorView.rootView as ViewGroup,
                activity = anchorView.getActivity(),
                anchorView,
                focusControlEnabled
            )
    }

    fun setOnDismissListener(onDismissListener: TooltipView.OnDismissListener?): Tooltip {
        tooltipView.setOnTooltipDismissListener(onDismissListener)
        return this
    }

    /**
     * Задает дополнительный горизональный отступ диалога. Может быть отрицательным.
     */
    fun setExtraHorizontalOffset(extraOffset: Int): Tooltip {
        tooltipView.setExtraHorizontalOffset(extraOffset)
        return this
    }

    /**
     * Задает дополнительный вертикальный отступ диалога. Может быть отрицательным.
     */
    fun setExtraVerticalOffset(extraOffset: Int): Tooltip {
        tooltipView.setExtraVerticalOffset(extraOffset)
        return this
    }

    /**
     * Задает позицию треугольника-указателя на якорь.
     */
    fun setPosition(position: Position): Tooltip {
        tooltipView.setPosition(position)
        return this
    }

    /**
     * Задает кастомную вьюху как контейнер.
     */
    fun setCustomView(view: View, @IdRes closeButtonId: Int): Tooltip {
        tooltipView.setCustomView(view, closeButtonId)
        return this
    }

    /**
     * Задает поведение при скролле якорной вьюхи.
     * По умолчанию [ScrollBehavior.MOVE].
     */
    fun setScrollBehavior(behavior: ScrollBehavior): Tooltip {
        scrollBehavior = behavior
        return this
    }

    /**
     * Возвращает true если Tooltip в данный момент отображается
     */
    fun isShown(): Boolean = tooltipView.isShown

    /**
     * Показывает попап.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun show(): TooltipView {
        tooltipView.translationZ = TRANSLATION_Z

        val prevTooltip = rootView.findViewWithTag<TooltipView>(TOOLTIP_VIEW_TAG)
        prevTooltip?.forceClose()

        val rect = Rect()
        if (showInSbisContainer) {
            view.obtainVisibleRectInParentCoords(
                rect,
                rootView
            )
        } else {
            view.getGlobalVisibleRect(rect)
        }
        val localViewRect = Rect(rect)

        val rootGlobalRect = Rect()
        val rootGlobalOffset = Point()
        rootView.getGlobalVisibleRect(rootGlobalRect, rootGlobalOffset)

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        rect.left = location[0]

        if (isShowHelperView) {
            rootView.addView(
                helperClickableView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        rootView.addView(tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val viewTreeObserver = tooltipView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                tooltipView.setup(localViewRect, rect, rootView.width)
                viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })

        setupTooltipAdjustmentToAnchorMovement()

        if (focusControlEnabled) {
            view.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) tooltipView.forceClose()
            }
        }

        helperClickableView.setOnTouchListener { v, event ->
            tooltipView.forceClose()
            false
        }

        return tooltipView
    }

    private fun setupTooltipAdjustmentToAnchorMovement() {
        val adjuster = TooltipToAnchorAdjuster()
        adjuster.attach()
        tooltipView.doOnDetach {
            adjuster.detach()
        }
    }

    /**
     * Закрывает подсказку
     */
    fun hide() {
        tooltipView.forceClose()
    }

    /**
     * Задает паддинги для попапа.
     */
    @Suppress("unused")
    fun setPadding(left: Int, top: Int, right: Int, bottom: Int): Tooltip {
        tooltipView.setPadding(left, top, right, bottom)
        return this
    }

    /**
     * Устанавливает флаг который решает нужно ли отображать view-помощник для перехвата кликов вне подсказки или нет.
     * Этот метод нужно вызывать до метода show()
     */
    fun setIsShowHelperView(isShowHelperView: Boolean): Tooltip {
        this.isShowHelperView = isShowHelperView
        return this
    }

    /**
     * Задает текст для отображения.
     */
    fun setText(text: String): Tooltip {
        tooltipView.setText(text)
        return this
    }

    /**
     * Задает ресурс изображения справа от текста.
     */
    fun setRightIconRes(@DrawableRes drawableRes: Int): Tooltip {
        tooltipView.setRightIconRes(drawableRes)
        return this
    }

    /**
     * Задает позицию треугольничка на грани.
     */
    fun setPointerPosition(pointerPosition: PointerPosition): Tooltip {
        tooltipView.setPointerPosition(pointerPosition)
        return this
    }

    /**
     * Задает состояние тултипа (из макета http://axure.tensor.ru/themes2/%D0%B2%D1%81%D0%BF%D0%BB%D1%8B%D0%B2%D0%B0%D1%8E%D1%89%D0%B0%D1%8F_%D0%BF%D0%BE%D0%B4%D1%81%D0%BA%D0%B0%D0%B7%D0%BA%D0%B0_0_2.html).
     */
    fun setState(state: State): Tooltip {
        tooltipView.setState(state)
        return this
    }

    /**
     * Задает цвет акцентной границы тултипа (из макета http://axure.tensor.ru/themes2/%D0%B2%D1%81%D0%BF%D0%BB%D1%8B%D0%B2%D0%B0%D1%8E%D1%89%D0%B0%D1%8F_%D0%BF%D0%BE%D0%B4%D1%81%D0%BA%D0%B0%D0%B7%D0%BA%D0%B0_0_2.html).
     */
    fun setAccentBorderColor(@AttrRes colorAttr: Int): Tooltip {
        tooltipView.setAccentBorderColor(colorAttr)
        return this
    }

    enum class PointerPosition {
        START,
        CENTER,
        END
    }

    /**
     * Поведение при скролле
     */
    enum class ScrollBehavior {
        /**
         * Двигаться вместе с прокручиваемой вью
         */
        MOVE,

        /**
         * Закрываться
         */
        CLOSE
    }

    enum class State {
        DEFAULT,
        ERROR,
        HIGHLIGHTED
    }

    /**
     * Помещает в [rect] координаты области [View] в системе координат родителя
     */
    private fun View.obtainVisibleRectInParentCoords(rect: Rect, parent: ViewGroup) {
        getLocalVisibleRect(rect)
        parent.offsetDescendantRectToMyCoords(this, rect)
    }

    /**
     * Подгоняет положение Tooltip под сдвиги его якорного View.
     */
    private inner class TooltipToAnchorAdjuster :
        ViewTreeObserver.OnGlobalLayoutListener,
        ViewTreeObserver.OnScrollChangedListener {

        private var lastAnchorPosition: Point? = null

        // Чтобы не создавать каждый раз
        private val zeroOffset = Point(0, 0)
        private val dummyRect = Rect()

        /**
         * Подключает как слушателя в [ViewTreeObserver].
         * Должен быть отключен через [detach], чтобы избежать утечек.
         */
        fun attach() {
            lastAnchorPosition = getAnchorGlobalPosition()

            view.viewTreeObserver.takeIf { it.isAlive }?.apply {
                addOnGlobalLayoutListener(this@TooltipToAnchorAdjuster)
                addOnScrollChangedListener(this@TooltipToAnchorAdjuster)
            }
        }

        /** @SelfDocumented */
        fun detach() {
            view.viewTreeObserver.takeIf { it.isAlive }?.apply {
                removeOnGlobalLayoutListener(this@TooltipToAnchorAdjuster)
                removeOnScrollChangedListener(this@TooltipToAnchorAdjuster)
            }

            lastAnchorPosition = null
        }

        override fun onGlobalLayout() {
            val offset = calcPositionOffset()
            moveTooltipBy(offset)
        }

        override fun onScrollChanged() {
            val offset = calcPositionOffset()
            if (offset == zeroOffset) {
                return
            }

            when (scrollBehavior) {
                ScrollBehavior.MOVE -> moveTooltipBy(offset)
                ScrollBehavior.CLOSE -> tooltipView.forceClose()
            }
        }

        private fun moveTooltipBy(offset: Point) {
            tooltipView.translationX += offset.x
            tooltipView.translationY += offset.y
        }

        private fun calcPositionOffset(): Point {
            val newPosition = getAnchorGlobalPosition() ?: return zeroOffset
            val oldPosition = lastAnchorPosition ?: newPosition
            lastAnchorPosition = newPosition

            return Point(
                newPosition.x - oldPosition.x,
                newPosition.y - oldPosition.y
            )
        }

        private fun getAnchorGlobalPosition() =
            Point().takeIf { view.getGlobalVisibleRect(dummyRect, it) } // getLocationOnScreen запаздывает
    }
}