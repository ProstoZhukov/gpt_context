package ru.tensor.sbis.main_screen_decl.env

import android.graphics.drawable.Drawable
import android.view.View
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import java.util.Date

/**
 * Расширенный интерфейс [BottomBarProvider]
 *
 * @author kv.martyshenko
 */
interface BottomBarProviderExt : BottomBarProvider, BottomBarContainer {

    // region Icons
    fun setNavigationFabIcon(drawable: Drawable?)

    fun setExtraFabIcon(drawable: Drawable?)

    fun setExtraFab2Icon(drawable: Drawable?)

    fun setExtraFab3Icon(drawable: Drawable?)

    fun setExtraFab4Icon(drawable: Drawable?)

    fun setTodayFabIcon(drawable: Drawable?)
    // endregion

    fun resetNavigationFabClickListener() {
        setNavigationFabClickListener(null)
    }

    override fun setTodayExtraFabClickListener(extraFabClickListener: View.OnClickListener?)

    override fun showTodayExtraFabButton(date: Date, isWorkDay: Boolean, animated: Boolean)

    override fun hideTodayExtraFabButton(animated: Boolean)

    // region Visibility
    fun isNavigationFabShown(): Boolean

    fun isExtraFabShown(): Boolean

    fun isExtraFab2Shown(): Boolean

    fun isExtraFab3Shown(): Boolean

    fun isExtraFab4Shown(): Boolean
    // endregion

    // region Style
    fun setNavigationFabStyle(style: SbisButtonStyle) = Unit

    fun resetNavigationFabStyle() = Unit

    fun setExtraFabStyle(style: SbisButtonStyle) = Unit

    fun setExtraFab2Style(style: SbisButtonStyle) = Unit

    fun setExtraFab3Style(style: SbisButtonStyle) = Unit

    fun setExtraFab4Style(style: SbisButtonStyle) = Unit
    // endregion

    val mainFab: View

    val extraFabContainer: View?
}