package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager

/**
 * LayoutManager, в который добавление элементов происходит справа на лево.
 * Примечание: При использовании декоратора следует менять местами left и right отступы.
 *
 * @author ra.geraskin
 */
internal class LeftOrientationGidLayoutManager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    GridLayoutManager(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * @SelfDocumented
     */
    override fun isLayoutRTL(): Boolean = true

}