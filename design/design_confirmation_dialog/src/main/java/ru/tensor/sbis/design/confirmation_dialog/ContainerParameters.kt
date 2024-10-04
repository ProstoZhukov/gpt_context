package ru.tensor.sbis.design.confirmation_dialog

import androidx.annotation.DimenRes
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.design_confirmation.R

/**
 * Параметры контейнера для диалога подтверждения
 * @param customWidth пользовательская ширина диалога
 *
 * @author ma.kolpakov
 */
data class ContainerParameters(
    @DimenRes internal val customWidth: Int = R.dimen.design_confirmation_dialog_width,
    internal var dimType: DimType = DimType.SOLID,
    internal val shouldDismissByTapOutside: Boolean = true,
)