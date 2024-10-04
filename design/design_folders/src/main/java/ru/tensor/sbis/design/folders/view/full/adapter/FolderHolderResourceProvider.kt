package ru.tensor.sbis.design.folders.view.full.adapter

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.view.full.FolderListViewMode
import ru.tensor.sbis.design.theme.global_variables.Offset

/***
 * Провайдер ресурсов. Нужен для отделения логики получения ресурсов от view.
 *
 * @param context контекст для доступа к ресурсам
 *
 * @author ma.kolpakov
 */
internal class FolderHolderResourceProvider(
    context: Context,
    private val viewMode: FolderListViewMode
) {

    var pressedItemColor: Pair<IntArray, Int>? = null

    /**
     * Флаг принудительного скрытия иконки показа компактной панели папок.
     */
    var isShownLeftFolderIcon = true

    private val resources = context.resources

    @Px
    private val iconSize = Offset.XS.getDimenPx(context) * 2 +
        SbisRoundButtonSize.S.globalVar.getDimenPx(context) +
        Offset.M.getDimenPx(context)

    @DimenRes
    val defaultStateIconSizeRes = R.dimen.design_folders_action_icon_size

    @DimenRes
    val itemLeftPaddingRes = R.dimen.design_folders_full_list_left_padding

    /** @SelfDocumented */
    fun getDefaultStateIconSize(): Float = getDimen(defaultStateIconSizeRes)

    /** @SelfDocumented */
    fun getFirstItemLeftPaddingPx(): Int = when (viewMode) {
        FolderListViewMode.STAND_ALONE -> getDimenPx(R.dimen.design_folders_full_folder_horizontal_padding)
        FolderListViewMode.NESTED -> if (isShownLeftFolderIcon) iconSize
        else getDimenPx(R.dimen.design_folders_full_folder_horizontal_padding_without_icon)
    }

    /** @SelfDocumented */
    fun getItemLeftPaddingPx(): Int = getDimenPx(itemLeftPaddingRes)

    /** @SelfDocumented */
    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int =
        ResourcesCompat.getColor(resources, colorRes, null)

    /** @SelfDocumented */
    @Dimension
    fun getDimen(@DimenRes dimenRes: Int): Float =
        resources.getDimension(dimenRes)

    /** @SelfDocumented */
    @Px
    fun getDimenPx(@DimenRes dimenRes: Int): Int =
        resources.getDimensionPixelSize(dimenRes)

    fun initStyle(
        context: Context,
        attributeSet: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defaultStyle: Int
    ) {
        context.withStyledAttributes(attributeSet, R.styleable.FoldersView, defStyleAttr, defaultStyle) {
            pressedItemColor = intArrayOf(android.R.attr.state_pressed) to
                getColor(R.styleable.FoldersView_FoldersView_pressedItemColor, Color.BLACK)
        }
    }
}
