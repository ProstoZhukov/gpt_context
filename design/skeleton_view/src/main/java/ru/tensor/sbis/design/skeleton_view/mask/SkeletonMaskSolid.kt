package ru.tensor.sbis.design.skeleton_view.mask

import android.view.View
import androidx.annotation.ColorInt

/**
 * Маска с равномерной заливкой
 *
 * @param parentView родительское view
 * @param color цвет маски
 *
 * @author us.merzlikina
 */
internal class SkeletonMaskSolid(
    parentView: View,
    @ColorInt color: Int
) : SkeletonMask(parentView, color)