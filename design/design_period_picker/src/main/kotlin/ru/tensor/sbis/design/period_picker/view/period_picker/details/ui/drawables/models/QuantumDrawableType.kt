package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Тип отрисовки кванта.
 *
 * @author mb.kruglova
 */
@Parcelize
internal sealed class QuantumDrawableType : Parcelable {

    /** Отрисовка по умолчанию. */
    object DefaultDrawableType : QuantumDrawableType()

    /** Отрисовка кванта из выбранного периода,
     * если в периоде всего один квант либо этот квант стоит отдельно от других квантов из выбранного периода.
     */
    object SingleDrawableType : QuantumDrawableType()

    /** Отрисовка кванта, расположенного слева от других квантов, входящих в период. */
    class LeftDrawableType(val bound: BoundaryDrawableType) : QuantumDrawableType()

    /** Отрисовка кванта, расположенного между другими квантами, входящих в период. */
    class InternalDrawableType(val center: CentralDrawableType) : QuantumDrawableType()

    /** Отрисовка кванта, расположенного справа от других квантов, входящих в период. */
    class RightDrawableType(val bound: BoundaryDrawableType) : QuantumDrawableType()
}