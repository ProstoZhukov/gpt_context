package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumDrawableType.*

/**
 * Тип расположения кванта.
 *
 * @author mb.kruglova
 */
internal enum class QuantumPlacementType(val drawableType: QuantumDrawableType) {

    /** Квант, расположенный отдельно от других выбранных квантов. */
    SINGLE(SingleDrawableType),

    /** Квант, у которого справа и слева есть другие кванты, входящие в выбранный период. */
    IN_LINE(InternalDrawableType(CentralDrawableType.VERTICAL_BORDER)),

    /** Квант, у которого только сверху есть квант, входящих в выбранный период. */
    TOP(LeftDrawableType(BoundaryDrawableType.VERTICAL_ROUNDING)),

    /** Квант, у которого только снизу есть квант, входящих в выбранный период. */
    BOTTOM(RightDrawableType(BoundaryDrawableType.VERTICAL_ROUNDING)),

    /** Квант, у которого только слева есть квант, входящих в выбранный период. */
    LEFT(RightDrawableType(BoundaryDrawableType.HORIZONTAL_ROUNDING)),

    /** Квант, у которого только справа есть квант, входящих в выбранный период. */
    RIGHT(LeftDrawableType(BoundaryDrawableType.HORIZONTAL_ROUNDING)),

    /** Квант, у которого сверху и слева есть кванты, входящих в выбранный период. */
    TOP_LEFT(RightDrawableType(BoundaryDrawableType.BOTTOM_ROUNDING)),

    /** Квант, у которого сверху и справа есть кванты, входящих в выбранный период. */
    TOP_RIGHT(LeftDrawableType(BoundaryDrawableType.BOTTOM_ROUNDING)),

    /** Квант, у которого снизу и слева есть кванты, входящих в выбранный период. */
    BOTTOM_LEFT(RightDrawableType(BoundaryDrawableType.TOP_ROUNDING)),

    /** Квант, у которого снизу и справа есть кванты, входящих в выбранный период. */
    BOTTOM_RIGHT(LeftDrawableType(BoundaryDrawableType.TOP_ROUNDING)),

    /** Квант, у которого сверху нет квантов, входящих в выбранный период. */
    NO_TOP(InternalDrawableType(CentralDrawableType.TOP_BORDER)),

    /** Квант, у которого снизу нет квантов, входящих в выбранный период. */
    NO_BOTTOM(InternalDrawableType(CentralDrawableType.BOTTOM_BORDER)),

    /** Квант, у которого слева нет квантов, входящих в выбранный период. */
    NO_LEFT(LeftDrawableType(BoundaryDrawableType.BORDER)),

    /** Квант, у которого справа нет квантов, входящих в выбранный период. */
    NO_RIGHT(RightDrawableType(BoundaryDrawableType.BORDER)),

    /** Квант, окруженный со всех сторон квантами, входящими в выбранный периода. */
    MULTIPLE(InternalDrawableType(CentralDrawableType.NO_BORDER))
}