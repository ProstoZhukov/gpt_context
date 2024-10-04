package ru.tensor.sbis.design.toolbar.appbar.behavior

import android.content.Context
import android.util.AttributeSet
import ru.tensor.sbis.design.toolbar.appbar.behavior.delegate.SbisAppBarAlwaysEnableScrollBehaviorDelegate

/**
 * Реализация [SbisAppBarScrollingViewBehavior], определяющая возможность сворачивания шапки когда содержимое
 * представляет набор вкладок.
 *
 * @author us.bessonov
 */
@Suppress("unused")
class SbisAppBarViewPagerBehavior : SbisAppBarScrollingViewBehavior {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * Если на экране с графической шапкой представлен набор вкладок, возможность сворачивания шапки доступна всегда,
     * поскольку содержимое вкладок может различаться по высоте.
     */
    override val delegate = SbisAppBarAlwaysEnableScrollBehaviorDelegate()

}