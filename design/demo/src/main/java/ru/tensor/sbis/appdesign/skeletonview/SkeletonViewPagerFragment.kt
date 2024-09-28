package ru.tensor.sbis.appdesign.skeletonview

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.skeleton_view.Skeleton

/**
 * Базовый абстрактный класс для создания Fragment
 *
 * @author us.merzlikina
 */
abstract class SkeletonViewPagerFragment(
    @LayoutRes private val layoutResId: Int,
    val title: String
) : Fragment(layoutResId) {

    abstract val skeleton: Skeleton
}