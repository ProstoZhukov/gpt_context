package ru.tensor.sbis.design.design_menu.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.text.TextUtils
import android.view.View
import androidx.core.view.ViewCompat
import ru.tensor.sbis.design.design_menu.R
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.EMPTY_STRING
import ru.tensor.sbis.design.design_menu.model.ItemSelectionState.CHECKED
import ru.tensor.sbis.design.design_menu.utils.ContainerType
import ru.tensor.sbis.design.design_menu.utils.ContainerType.CONTAINER
import ru.tensor.sbis.design.design_menu.utils.ContainerType.PANEL
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.HorizontalPosition.RIGHT
import ru.tensor.sbis.design.theme.HorizontalPosition.LEFT
import ru.tensor.sbis.design.design_menu.SbisMenuItem
import ru.tensor.sbis.design.design_menu.utils.MenuItemViewAccessibilityDelegate
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.VIEW_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.VIEW_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.LEFT_ICON_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.LEFT_ICON_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.RIGHT_ICON_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.RIGHT_ICON_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.RIGHT_ICON_END_PANEL
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.LEFT_MARKER_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.LEFT_MARKER_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.RIGHT_MARKER_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.RIGHT_MARKER_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.ARROW_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.ARROW_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.TITLE_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.TITLE_END
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.VIEW_VERTICAL

/**
 * View класс для обычного элемента меню [SbisMenuItem].
 *
 * @author ra.geraskin
 */
@SuppressLint("ViewConstructor")
internal class MenuItemView(
    private val ctx: Context,
    private val sh: SbisMenuStyleHolder,
    private val markerAlignment: HorizontalPosition,
    private val containerType: ContainerType,
    private val twoLinesItemsTitle: Boolean
) : View(ctx) {

    private var selectionEnable: Boolean = false
    private var iconAlignment: HorizontalPosition = LEFT
    private var emptyIconSpaceEnabled: Boolean = false
    private val minHeight = sh.itemMinHeight
    private var hierarchyOffset: Int = 0

    @TestOnly
    internal val titleLayout: TextLayout = TextLayout().apply { id = R.id.menu_item_title }

    @TestOnly
    internal val commentLayout: TextLayout = TextLayout().apply { id = R.id.menu_item_comment }

    @TestOnly
    internal val iconLayout: TextLayout = TextLayout().apply { id = R.id.menu_item_icon }

    @TestOnly
    internal val arrowIconLayout: TextLayout = TextLayout().apply { id = R.id.menu_item_icon_sub_menu_arrow }

    @TestOnly
    internal val markerLayout: TextLayout = TextLayout().apply { id = R.id.menu_item_marker }

    init {
        id = R.id.menu_item_root
        MenuItemViewAccessibilityDelegate(
            this, setOf(titleLayout, commentLayout, iconLayout, arrowIconLayout, markerLayout)
        ).apply {
            ViewCompat.setAccessibilityDelegate(this@MenuItemView, this)
        }
    }

    /**
     * @SelfDocumented
     */
    fun setParams(item: BaseMenuItem, hasMenuItems: Boolean, selectionEnable: Boolean) {
        setItemBackground(sh)
        this.iconAlignment = item.settings.iconAlignment
        this.emptyIconSpaceEnabled = item.settings.emptyIconSpaceEnabled
        this.selectionEnable = selectionEnable
        titleLayout.configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(ctx)
            needHighWidthAccuracy = true
            paint.color = item.settings.titleColor?.getColor(ctx) ?: sh.titleColor
            paint.textSize = sh.titleSize
            text = item.title ?: EMPTY_STRING
            maxLines = if (twoLinesItemsTitle) sh.titleMaxLines else 1
            ellipsize = TextUtils.TruncateAt.END
            textAlignment = TEXT_ALIGNMENT_TEXT_START
        }
        commentLayout.configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(ctx)
            paint.color = sh.commentColor
            paint.textSize = sh.commentSize
            text = item.subTitle ?: EMPTY_STRING
            maxLines = sh.commentMaxLines
            needHighWidthAccuracy = true
            textAlignment = TEXT_ALIGNMENT_TEXT_START
            ellipsize = TextUtils.TruncateAt.END
        }
        iconLayout.configure {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(ctx)
            paint.textSize = sh.iconSize
            paint.color = when {
                item.icon != null -> item.settings.iconColor?.getColor(ctx) ?: sh.iconColor
                else -> Color.TRANSPARENT
            }
            text = when {
                item.icon != null -> item.icon!!.character.toString()
                item.settings.emptyIconSpaceEnabled -> SbisMobileIcon.Icon.smi_Google.character.toString()
                else -> EMPTY_STRING
            }
        }
        arrowIconLayout.configure {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(ctx)
            paint.textSize = sh.subMenuArrowIconSize
            paint.color = sh.subMenuArrowIconColor
            text = if (hasMenuItems) sh.subMenuArrowIcon.character.toString() else EMPTY_STRING
            paint.color = if (item is SbisMenu) sh.subMenuArrowIconColor else Color.TRANSPARENT
        }
        markerLayout.configure {
            paint.textSize = sh.markerSize
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(ctx)
            text = if (selectionEnable) sh.menuSelectionStyle.getIcon().character.toString() else EMPTY_STRING
            paint.color = if (item.settings.selectionState == CHECKED) {
                sh.menuSelectionStyle.getIconColor(ctx)
            } else {
                Color.TRANSPARENT
            }
            this.padding = sh.menuSelectionStyle.getTextLayoutPadding(ctx)
        }
        hierarchyOffset = sh.hierarchyOffset * item.hierarchyLevel
    }

    private val markerEnable: Boolean
        get() = markerLayout.text != EMPTY_STRING || selectionEnable

    private val markerLeftEnable: Boolean
        get() = markerEnable && markerAlignment == LEFT

    private val markerEnableRight: Boolean
        get() = markerEnable && markerAlignment == RIGHT

    private val iconEnable: Boolean
        get() = iconLayout.text != EMPTY_STRING || emptyIconSpaceEnabled

    private val iconEnableLeft: Boolean
        get() = iconEnable && iconAlignment == LEFT

    private val iconEnableRight: Boolean
        get() = iconEnable && iconAlignment == RIGHT

    private val arrowEnable: Boolean
        get() = arrowIconLayout.text != EMPTY_STRING

    private val commentEnable: Boolean
        get() = commentLayout.text != EMPTY_STRING

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)

        if (availableWidth == 0) {
            val width = initialMeasureWidthForMenuLayout()
            setMeasuredDimension(width, measureHeight())
        } else {
            measureWidth(availableWidth)
            setMeasuredDimension(availableWidth, measureHeight())
        }
    }

    private fun initialMeasureWidthForMenuLayout() = measureWidth(sh.maxItemWidth)

    /**
     * Расчёт ширины элемента в зависимости от максимальной ширины. Зависимость от максимальной ширины нужна, т.к.
     * из-за нехватки места заголовок может быть пересчитан на две строки, тогда итоговая ширина изменится.
     */
    fun measureWidth(maxWidth: Int): Int {
        var width = 0

        width += VIEW_START.get(ctx) + VIEW_END.get(ctx)

        width += hierarchyOffset

        when {
            iconEnableLeft -> width += (LEFT_ICON_START.get(ctx) + iconLayout.width + LEFT_ICON_END.get(ctx))

            iconEnableRight -> {
                val iconPaddingEnd = if (containerType == PANEL) RIGHT_ICON_END_PANEL else RIGHT_ICON_END
                width += (RIGHT_ICON_START.get(ctx) + iconLayout.width + iconPaddingEnd.get(ctx))
            }
        }

        if (selectionEnable) {
            width += when (markerAlignment) {
                LEFT -> (LEFT_MARKER_START.get(ctx) + markerLayout.width + LEFT_MARKER_END.get(ctx))
                RIGHT -> (RIGHT_MARKER_START.get(ctx) + markerLayout.width + RIGHT_MARKER_END.get(ctx))
            }
        }

        if (arrowEnable)
            width += (ARROW_START.get(ctx) + arrowIconLayout.width + ARROW_END.get(ctx))

        val availableWidth = maxWidth - width - TITLE_START.get(ctx) - TITLE_END.get(ctx)

        titleLayout.configure {
            this.maxWidth = availableWidth
        }

        width += TITLE_START.get(ctx) + titleLayout.width + TITLE_END.get(ctx)

        // Комментарий занимает всю доступную ширину от начала заголовка, до маркера выделения (если он есть).
        // Расчёт нужно производить сейчас, чтобы понять, вместится ли комментарий в одну или две строки, чтобы далее
        // рассчитать высоту элемента.
        commentLayout.configure {
            var commentWidth = maxWidth
            if (iconEnableLeft)
                commentWidth -= (LEFT_ICON_START.get(ctx) + iconLayout.width + LEFT_ICON_END.get(ctx))
            if (markerEnable)
                commentWidth -= (RIGHT_MARKER_START.get(ctx) + markerLayout.width + RIGHT_MARKER_END.get(ctx))
            if (arrowEnable)
                commentWidth -= (ARROW_START.get(ctx) + arrowIconLayout.width + ARROW_END.get(ctx))

            commentWidth -= (TITLE_START.get(ctx) + VIEW_START.get(ctx) + VIEW_END.get(ctx))
            this.maxWidth = commentWidth
        }

        return width
    }

    private fun measureHeight(): Int {
        if (titleLayout.lineCount == 1 && commentLayout.text == EMPTY_STRING) return minHeight
        var height = 0
        height += VIEW_VERTICAL.get(ctx)
        height += titleLayout.height
        if (commentEnable) height += commentLayout.height
        height += VIEW_VERTICAL.get(ctx)
        return height

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var width = VIEW_START.get(ctx)

        width += hierarchyOffset

        if (markerLeftEnable) {
            width += LEFT_MARKER_START.get(ctx)
            val markerTop = (measuredHeight - markerLayout.height) / 2
            markerLayout.layout(width, markerTop)
            width += markerLayout.width + LEFT_MARKER_END.get(ctx)
        }

        if (iconEnableLeft) {
            width += LEFT_ICON_START.get(ctx)
            val iconTop = VIEW_VERTICAL.get(ctx) + (titleLayout.getDesiredHeight() - iconLayout.height) / 2
            iconLayout.layout(width, iconTop)
            width += iconLayout.width + LEFT_ICON_END.get(ctx)
        }

        width += TITLE_START.get(ctx)
        titleLayout.layout(width, VIEW_VERTICAL.get(ctx))
        width += titleLayout.width + TITLE_END.get(ctx)

        // Далее для контейнера расчёт идёт от заголовка вправо, для шторки расчёт далее идёт с правого конца влево до заголовка.
        when (containerType) {
            CONTAINER -> {
                // Иконка           - прилегает к тексту
                // Стрелка подменю  - прилегает к правому краю меню
                // Маркер           - прилегает к стрелке подменю, или к правому краю меню
                if (iconEnableRight) {
                    width += RIGHT_ICON_START.get(ctx)
                    val iconTop = VIEW_VERTICAL.get(ctx) + (titleLayout.getDesiredHeight() - iconLayout.height) / 2
                    iconLayout.layout(width, iconTop)
                    width += iconLayout.width + RIGHT_ICON_END.get(ctx)
                }

                var endWidth = right - VIEW_END.get(ctx)

                if (arrowEnable) {
                    endWidth -= (arrowIconLayout.width + ARROW_END.get(ctx))
                    val arrowTop = (measuredHeight - arrowIconLayout.height) / 2
                    arrowIconLayout.layout(endWidth, arrowTop)
                    endWidth -= ARROW_START.get(ctx)
                }

                if (markerEnableRight) {
                    endWidth -= (markerLayout.width + RIGHT_MARKER_END.get(ctx))
                    val markerTop = (measuredHeight - markerLayout.height) / 2
                    markerLayout.layout(endWidth, markerTop)
                    endWidth -= RIGHT_MARKER_START.get(ctx)
                }

            }

            PANEL -> {
                // Стрелка подменю  - прилегает к правому краю меню
                // Маркер           - прилегает к стрелке меню, или к правому краю меню
                // Иконка           - прилегает к маркеру, или к стрелке подменю, или к правому краю меню
                var endWidth = right - VIEW_END.get(ctx)

                if (arrowEnable) {
                    endWidth -= (arrowIconLayout.width + ARROW_END.get(ctx))
                    val arrowTop = (measuredHeight - arrowIconLayout.height) / 2
                    arrowIconLayout.layout(endWidth, arrowTop)
                    endWidth -= ARROW_START.get(ctx)
                }

                if (markerEnableRight) {
                    endWidth -= (markerLayout.width + RIGHT_MARKER_END.get(ctx))
                    val markerTop = (measuredHeight - markerLayout.height) / 2
                    markerLayout.layout(endWidth, markerTop)
                    endWidth -= RIGHT_MARKER_START.get(ctx)
                }

                if (iconEnableRight) {
                    endWidth -= (iconLayout.width + RIGHT_ICON_END_PANEL.get(ctx))
                    val iconTop = VIEW_VERTICAL.get(ctx) + (titleLayout.getDesiredHeight() - iconLayout.height) / 2
                    iconLayout.layout(endWidth, iconTop)
                    endWidth -= RIGHT_ICON_START.get(ctx)
                }
            }
        }
        if (commentEnable) {
            commentLayout.layout(titleLayout.left, titleLayout.bottom)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        titleLayout.draw(canvas)
        commentLayout.draw(canvas)
        iconLayout.draw(canvas)
        markerLayout.draw(canvas)
        arrowIconLayout.draw(canvas)
    }

    private fun setItemBackground(styleHolder: SbisMenuStyleHolder) = with(StateListDrawable()) {
        addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(styleHolder.itemBackgroundPressedColor))
        addState(intArrayOf(), ColorDrawable(styleHolder.itemBackgroundColor))
        background = this
    }
}