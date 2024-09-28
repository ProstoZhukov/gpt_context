@file:Suppress("KDocUnresolvedReference")

package ru.tensor.sbis.base_components.fragment.selection

import android.graphics.Point
import android.util.TypedValue
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.base_components.R
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.utils.R as RDesign

/**
 * Уменьшает размеры [DialogFragment] на планшете в соответствии с шириной [SelectionWindowFragment]
 *
 * @param shrinkVertically должна ли помимо ширины уменьшаться и высота
 */
@JvmOverloads
fun DialogFragment.shrinkDialogOnTablet(shrinkVertically: Boolean = false) {
    with(dialog?.window) {
        if (DeviceConfigurationUtils.isTablet(requireContext())) {
            val point = Point()
            this!!.windowManager.defaultDisplay.getSize(point)

            val widthRatio = TypedValue().apply {
                resources.getValue(RDesign.integer.tablet_selection_window_width_percent, this, true)
            }.float
            val heightRatio = TypedValue().apply {
                resources.getValue(R.integer.base_components_shrinked_dialog_height_percent, this, true)
            }.float

            val height = if (shrinkVertically) {
                (point.y * heightRatio).toInt()
            } else {
                ViewGroup.LayoutParams.MATCH_PARENT
            }

            setLayout((point.x * widthRatio).toInt(), height)
        } else {
            this!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}
