package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy

/**
 * Стратегия компоновки заглушки без отступов по вертикали для портретного режима.
 *
 * Отличается от [PortraitStubViewComposer] только отсутствием отступа сверху и снизу.
 *
 * @see PortraitStubViewComposer
 * @see LandscapeStubViewComposer
 * @see LandscapeBlockStubViewComposer
 *
 * @author ma.kolpakov
 */
internal class LandScapeDensStubViewComposer(
    icon: View?,
    message: SbisTextView,
    details: TextView,
    iconMeasuringStrategy: IconMeasuringStrategy,
    context: Context,
) : LandscapeStubViewComposer(icon, message, details, iconMeasuringStrategy, context) {

    @Px
    override val stubViewTopPadding: Int = 0

    @Px
    override val stubViewPaddingVertical = 0
}