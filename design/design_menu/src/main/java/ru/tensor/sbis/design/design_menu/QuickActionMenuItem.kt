package ru.tensor.sbis.design.design_menu

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Реализация элемента для меню быстрого действия.
 *
 * @author ra.geraskin
 */
class QuickActionMenuItem(
    val title: SbisString,
    val image: SbisMobileIcon.Icon? = null,
    val titleColor: SbisColor? = null,
    val imageColor: SbisColor? = null,
    var handler: (() -> Unit)? = null
) {

    constructor(
        title: String,
        image: SbisMobileIcon.Icon? = null,
        titleColor: SbisColor? = null,
        imageColor: SbisColor? = null,
        handler: (() -> Unit)? = null
    ) : this(
        title = PlatformSbisString.Value(title),
        image = image,
        imageColor = imageColor,
        titleColor = titleColor,
        handler = handler
    )
}
