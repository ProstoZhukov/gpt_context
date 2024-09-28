package ru.tensor.sbis.swipeablelayout.api.menu

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Иконка пункта свайп-меню.
 * Может быть указана как id строки с иконкой из шрифта, непосредственно строки с иконкой, либо как
 * [SbisMobileIcon.Icon].
 *
 * @author us.bessonov
 */
class SwipeIcon private constructor(
    @StringRes private val iconRes: Int = ResourcesCompat.ID_NULL, private val icon: String? = null
) {

    constructor(@StringRes icon: Int) : this(iconRes = icon)

    constructor(icon: SbisMobileIcon.Icon) : this(icon = "${icon.character}")

    constructor(icon: String) : this(icon = icon, iconRes = ResourcesCompat.ID_NULL)

    /** @SelfDocumented */
    internal fun getIcon(resources: Resources) = icon ?: resources.getString(iconRes)
}
