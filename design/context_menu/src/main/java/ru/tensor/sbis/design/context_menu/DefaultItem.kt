package ru.tensor.sbis.design.context_menu

import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * TODO Удалить после отказа от использования.
 * https://dev.sbis.ru/opendoc.html?guid=69aba5a1-0d97-439e-b1d1-63edf9b1890e&client=3
 */
class DefaultItem @Deprecated("Используй MenuItem") constructor(
    title: String,
    image: SbisMobileIcon.Icon? = null,
    @ColorRes imageColor: Int = ResourcesCompat.ID_NULL,
    imageAlignment: HorizontalPosition = HorizontalPosition.RIGHT,
    destructive: Boolean = false,
    public override val discoverabilityTitle: String? = null,
    public override val hidden: Boolean = false,
    public override val disabled: Boolean = false,
    override var state: MenuItemState = MenuItemState.OFF,
    emptyImageAlignment: HorizontalPosition? = null,
    @ColorRes titleColor: Int = ResourcesCompat.ID_NULL,
    handler: (() -> Unit)? = null
) : BaseItem(
    PlatformSbisString.Value(title),
    image,
    SbisColor.Res(imageColor),
    imageAlignment,
    destructive,
    discoverabilityTitle,
    hidden,
    disabled,
    state,
    SbisColor.Res(titleColor),
    emptyImageAlignment,
    handler
)
