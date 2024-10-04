package ru.tensor.sbis.appdesign.context_menu

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.skeleton_view.Skeleton

/**
 * Базовый абстрактный класс для создания Fragment
 *
 * @author ma.kolpakov
 */
abstract class ContextMenuPagerFragment(
    @LayoutRes private val layoutResId: Int,
    val title: String
) : Fragment(layoutResId)