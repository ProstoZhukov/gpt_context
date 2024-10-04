package ru.tensor.sbis.design.context_menu

import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Стандартная реализация для элемента [SbisMenu].
 *
 * @author ma.kolpakov
 */
class MenuItem constructor(
    title: SbisString,
    image: SbisMobileIcon.Icon? = null,
    imageColor: SbisColor = SbisColor.Res(ResourcesCompat.ID_NULL),
    imageAlignment: HorizontalPosition = HorizontalPosition.RIGHT,
    destructive: Boolean = false,
    public override val discoverabilityTitle: String? = null,
    public override val hidden: Boolean = false,
    public override val disabled: Boolean = false,
    override var state: MenuItemState = MenuItemState.OFF,
    emptyImageAlignment: HorizontalPosition? = null,
    titleColor: SbisColor = SbisColor.Res(ResourcesCompat.ID_NULL),
    handler: (() -> Unit)? = null
) : BaseItem(
    title,
    image,
    imageColor,
    imageAlignment,
    destructive,
    discoverabilityTitle,
    hidden,
    disabled,
    state,
    titleColor,
    emptyImageAlignment,
    handler
) {

    constructor(
        title: String,
        image: SbisMobileIcon.Icon? = null,
        imageColor: SbisColor = SbisColor.Res(ResourcesCompat.ID_NULL),
        imageAlignment: HorizontalPosition = HorizontalPosition.RIGHT,
        destructive: Boolean = false,
        discoverabilityTitle: String? = null,
        hidden: Boolean = false,
        disabled: Boolean = false,
        state: MenuItemState = MenuItemState.OFF,
        emptyImageAlignment: HorizontalPosition? = null,
        titleColor: SbisColor = SbisColor.Res(ResourcesCompat.ID_NULL),
        handler: (() -> Unit)? = null
    ) : this(
        title = PlatformSbisString.Value(title),
        image = image,
        imageColor = imageColor,
        imageAlignment = imageAlignment,
        destructive = destructive,
        discoverabilityTitle = discoverabilityTitle,
        hidden = hidden,
        disabled = disabled,
        state = state,
        emptyImageAlignment = emptyImageAlignment,
        titleColor = titleColor,
        handler = handler
    )
}
