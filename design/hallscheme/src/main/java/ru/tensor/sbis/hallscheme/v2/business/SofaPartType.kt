package ru.tensor.sbis.hallscheme.v2.business

import androidx.annotation.DrawableRes
import ru.tensor.sbis.hallscheme.R

/**
 * Части дивана.
 */
internal enum class SofaPartType(
    @DrawableRes
    val iconResName: Int,
    @DrawableRes
    val icon3dResName: Int
) {
    /** Левая верхняя часть прямого дивана. */
    STRAIGHT_LEFT_TOP(
        R.drawable.hall_scheme_sofa_14x30_straight_left,
        R.drawable.hall_scheme_sofa_14x30_white_left
    ),
    /** Центральная верхняя часть прямого дивана. */
    STRAIGHT_TOP(
        R.drawable.hall_scheme_sofa_30x30_straight,
        R.drawable.hall_scheme_sofa_30x30_white
    ),
    /** Правая верхняя часть прямого дивана. */
    STRAIGHT_RIGHT_TOP(
        R.drawable.hall_scheme_sofa_14x30_straight_right,
        R.drawable.hall_scheme_sofa_14x30_white_right
    ),

    /** Левая верхняя часть секционного дивана. */
    SECTION_LEFT_TOP(
        R.drawable.hall_scheme_sofa_14x30_section_left,
        R.drawable.hall_scheme_sofa_14x30_brown_left
    ),
    /** Центральная верхняя часть секционного дивана. */
    SECTION_TOP(
        R.drawable.hall_scheme_sofa_30x30_section,
        R.drawable.hall_scheme_sofa_30x30_brown
    ),
    /** Правая верхняя часть секционного дивана. */
    SECTION_RIGHT_TOP(
        R.drawable.hall_scheme_sofa_14x30_section_right,
        R.drawable.hall_scheme_sofa_14x30_brown_right
    ),

    /** Левая нижняя часть прямого дивана. */
    STRAIGHT_LEFT_BOTTOM(
        R.drawable.hall_scheme_sofa_14x30_straight_left_bottom,
        R.drawable.hall_scheme_sofa_14x30_white_left_bottom
    ),
    /** Центральная нижняя часть прямого дивана. */
    STRAIGHT_BOTTOM(
        R.drawable.hall_scheme_sofa_30x30_straight_bottom,
        R.drawable.hall_scheme_sofa_30x30_white_bottom
    ),
    /** Правая нижняя часть прямого дивана. */
    STRAIGHT_RIGHT_BOTTOM(
        R.drawable.hall_scheme_sofa_14x30_straight_right_bottom,
        R.drawable.hall_scheme_sofa_14x30_white_right_bottom
    ),

    /** Левая нижняя часть секционного дивана. */
    SECTION_LEFT_BOTTOM(
        R.drawable.hall_scheme_sofa_14x30_section_left_bottom,
        R.drawable.hall_scheme_sofa_14x30_brown_left_bottom
    ),
    /** Центральная нижняя часть секционного дивана. */
    SECTION_BOTTOM(
        R.drawable.hall_scheme_sofa_30x30_section_bottom,
        R.drawable.hall_scheme_sofa_30x30_brown_bottom
    ),
    /** Правая нижняя часть секционного дивана. */
    SECTION_RIGHT_BOTTOM(
        R.drawable.hall_scheme_sofa_14x30_section_right_bottom,
        R.drawable.hall_scheme_sofa_14x30_brown_right_bottom
    ),

    /** Левая часть прямого углового дивана. */
    STRAIGHT_CORNER_LEFT(
        R.drawable.hall_scheme_sofa_30x70_straight_left,
        R.drawable.hall_scheme_sofa_30x70_white_left
    ),
    /** Правая часть прямого углового дивана. */
    STRAIGHT_CORNER_RIGHT(
        R.drawable.hall_scheme_sofa_30x70_straight_right,
        R.drawable.hall_scheme_sofa_30x70_white_right
    ),

    /** Левая часть секционного углового дивана. */
    SECTION_CORNER_LEFT(
        R.drawable.hall_scheme_sofa_30x70_section_left,
        R.drawable.hall_scheme_sofa_30x70_brown_left
    ),
    /** Правая часть секционного углового дивана. */
    SECTION_CORNER_RIGHT(
        R.drawable.hall_scheme_sofa_30x70_section_right,
        R.drawable.hall_scheme_sofa_30x70_brown_right
    )
}