package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy

/**
 * Стратегия компоновки заглушки в блоке для ландшафтного режима.
 *
 * Отличается от [LandscapeStubViewComposer] только фиксированным отступом сверху.
 *
 * @see PortraitStubViewComposer
 * @see PortraitBlockStubViewComposer
 * @see LandscapeStubViewComposer
 *
 * @author ma.kolpakov
 */
internal class LandscapeBlockStubViewComposer(
    icon: View?,
    message: SbisTextView,
    details: TextView,
    iconMeasuringStrategy: IconMeasuringStrategy,
    context: Context,
) : LandscapeStubViewComposer(icon, message, details, iconMeasuringStrategy, context) {

    @Px
    override val stubViewTopPadding: Int = stubViewPaddingVertical
}
