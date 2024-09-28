package ru.tensor.sbis.hallscheme.v2.business

import ru.tensor.sbis.hallscheme.R

/**
 * Типы стульев.
 * @author aa.gulevskiy
 */
internal enum class ChairType(
    val iconResFlatName: Int,
    val iconRes3dName: Int
) {
    /**
     * Стул сверху.
     */
    TOP(
        R.drawable.hall_scheme_chair_rect_top,
        R.drawable.hall_scheme_chair_top_3d
    ),
    /**
     * Стул слева.
     */
    LEFT(
        R.drawable.hall_scheme_chair_rect_left,
        R.drawable.hall_scheme_chair_rect_left_3d
    ),
    /**
     * Стул справа.
     */
    RIGHT(
        R.drawable.hall_scheme_chair_rect_right,
        R.drawable.hall_scheme_chair_rect_right_3d
    ),
    /**
     * Стул снизу.
     */
    BOTTOM(
        R.drawable.hall_scheme_chair_rect_bottom,
        R.drawable.hall_scheme_chair_rect_bottom_3d
    )
}