package ru.tensor.sbis.design.folders.data.model

import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.folders.R

/**
 * Тип дополнительной команды.
 *
 * @param icon иконка для отображения
 * @param size ресурс размера иконки
 * @param color ресурс цвета иконки
 *
 * @author ma.kolpakov
 */
enum class AdditionalCommandType(
    internal val icon: SbisMobileIcon.Icon? = null,
    @DimenRes internal val size: Int = ResourcesCompat.ID_NULL,
) {

    /** Пустая команда. Не отображается. */
    EMPTY,

    /** Обычный. Без иконки */
    DEFAULT,

    /** Расшарить. Иконка толстой стрелки вправо синего цвета */
    SHARE(
        icon = SbisMobileIcon.Icon.smi_Publish2,
        size = R.dimen.design_folders_action_icon_size,
    ),

    /** Отмена шаринга. Иконка крестика серого цвета и уменьшенного размера */
    CANCEL_SHARING(
        icon = SbisMobileIcon.Icon.smi_navBarClose,
        size = R.dimen.design_folders_action_icon_size_small,
    );

    /** @SelfDocumented */
    internal val hasIcon: Boolean = icon != null
}
