package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.view.View
import android.widget.TextView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.StubViewMode
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.DrawableIconLandscapeMeasuringStrategy
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.DrawableIconPortraitMeasuringStrategy
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.ViewIconMeasuringStrategy

/**
 * Фабрика для получения [StubViewComposer]
 *
 * @author ma.kolpakov
 */
internal object StubViewComposerFactory {

    /**
     * Получение [StubViewComposer] по заданным параметрам
     *
     * @param isLandscape true, если устройство в ландшафтном режиме
     * @param isTablet запущено ли приложение на планшете
     * @param isDrawable используется ли drawable иконка
     * @param displayMode режим отображения заглушки
     * @param icon иконка заглушки
     * @param message [SbisTextView] заголовка
     * @param details [TextView] описания
     * @param context контекст для доступа к ресурсам
     */
    fun createStubViewComposer(
        isLandscape: Boolean,
        isTablet: Boolean,
        isDrawable: Boolean,
        displayMode: StubViewMode,
        icon: View?,
        message: SbisTextView,
        details: TextView,
        context: Context,
    ): StubViewComposer {
        val isPortrait = !isLandscape || isTablet
        val iconMeasuringStrategy = getIconMeasuringStrategy(isDrawable, isPortrait)
        return when (displayMode) {
            StubViewMode.BASE  ->
                if (isPortrait)
                    PortraitStubViewComposer(icon, message, details, iconMeasuringStrategy, context)
                else
                    LandscapeStubViewComposer(icon, message, details, iconMeasuringStrategy, context)
            StubViewMode.BLOCK ->
                if (isPortrait)
                    PortraitBlockStubViewComposer(icon, message, details, iconMeasuringStrategy, context)
                else
                    LandscapeBlockStubViewComposer(icon, message, details, iconMeasuringStrategy, context)
            StubViewMode.DENS  ->
                if (isPortrait)
                    PortraitDensStubViewComposer(icon, message, details, iconMeasuringStrategy, context)
                else
                    LandScapeDensStubViewComposer(icon, message, details, iconMeasuringStrategy, context)
        }
    }

    private fun getIconMeasuringStrategy(isDrawable: Boolean, isPortrait: Boolean): IconMeasuringStrategy =
        when {
            isDrawable && !isPortrait  -> DrawableIconLandscapeMeasuringStrategy()
            isDrawable && isPortrait -> DrawableIconPortraitMeasuringStrategy()
            else                       -> ViewIconMeasuringStrategy()
        }
}
