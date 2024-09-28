package ru.tensor.sbis.swipeablelayout.swipeablevm

import android.view.View
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import ru.tensor.sbis.swipeablelayout.MenuItem

/**
 * Вьюмодель элемента свайп-меню.
 *
 * @author us.bessonov
 */
internal class SwipeMenuItemVm(
    override var id: Int,
    val clickAction: Runnable,
    val viewClickAction: ((View) -> Unit)?,
    @ColorInt val background: Int?,
    @ColorInt val iconColor: Int? = null,
    val icon: String = "",
    val symbol: LiveData<String>? = null,
    val label: String = "",
    val isLabelSingleLine: Boolean = true,
    val isLabelVisible: Boolean = true,
    val isClickPostponedUntilMenuClosed: Boolean = false,
    val isBackgroundTintEnabled: Boolean = true,
    val autotestsText: String? = null,
    val isFocusable: Boolean = true
) : MenuItem