package ru.tensor.sbis.design.rating.type

import androidx.annotation.Px
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * @author ps.smirnyh
 */
internal interface RatingIconProvider {

    /** Отступ между иконками в px. */
    @get:Px
    @setparam:Px
    var iconsOffset: Int

    /** @SelfDocumented */
    fun updateIcons(icons: List<TextLayout>)

    /** @SelfDocumented */
    fun updateIconSize(icons: List<TextLayout>)

    /** @SelfDocumented */
    fun createIcons(): List<TextLayout>
}