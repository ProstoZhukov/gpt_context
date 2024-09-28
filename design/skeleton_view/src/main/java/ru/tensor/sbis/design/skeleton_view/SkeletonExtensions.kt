@file:JvmName("SkeletonViewUtils")

package ru.tensor.sbis.design.skeleton_view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import ru.tensor.sbis.design.skeleton_view.recyclerview.SkeletonRecyclerView
import ru.tensor.sbis.design.skeleton_view.viewpager2.SkeletonViewPager2

private const val LIST_ITEM_COUNT_DEFAULT = 3

/**
 * Создаем новый Skeleton оборачивая переданное View в SkeletonView
 *
 * @receiver View, которое хотим обернуть в SkeletonView
 * @param config конфигурация анимации
 *
 * @author us.merzlikina
 */
@JvmOverloads
fun createSkeleton(
    view: View,
    config: SkeletonConfig = SkeletonConfig.default(view.context)
): Skeleton {
    // Если у View есть родитель, то заменяем его SkeletonView
    val parent = (view.parent as? ViewGroup)
    val index = parent?.indexOfChild(view) ?: 0
    val params = view.layoutParams

    parent?.removeView(view)

    val skeleton = SkeletonView(view, config)

    if (params != null) {
        skeleton.layoutParams = params
    }
    parent?.addView(skeleton, index)

    return skeleton
}

/**
 * Применяем новый Skeleton к переданному RecyclerView и обертываем ViewHolders'ы его itemViews в SkeletonViews
 *
 * @receiver RecyclerView, элементы которого нужно обернуть в SkeletonViews
 * @param listItemLayoutResId id ресурса, который будет использоваться в качестве маски в itemView
 * @param itemCount количество элементов для отображения
 * @param config конфигурация UI
 */
@JvmOverloads
fun createSkeleton(
    recyclerView: RecyclerView,
    @LayoutRes listItemLayoutResId: Int,
    itemCount: Int = LIST_ITEM_COUNT_DEFAULT,
    config: SkeletonConfig = SkeletonConfig.default(recyclerView.context)
): Skeleton = SkeletonRecyclerView(recyclerView, listItemLayoutResId, itemCount, config)

/**
 * Применяем новый Skeleton к переданному ViewPager2
 *
 * @param listItemLayoutResId id ресурса, который будет использоваться в качестве маски в itemView
 * @param itemCount количество элементов для отображения
 * @param config конфигурация UI
 */
@JvmOverloads
fun createSkeleton(
    viewPager2: ViewPager2,
    @LayoutRes listItemLayoutResId: Int,
    itemCount: Int = LIST_ITEM_COUNT_DEFAULT,
    config: SkeletonConfig = SkeletonConfig.default(viewPager2.context)
): Skeleton = SkeletonViewPager2(viewPager2, listItemLayoutResId, itemCount, config)

internal fun ViewGroup.views(): List<View> = (0 until childCount).map { child -> getChildAt(child) }
