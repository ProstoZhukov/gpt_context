package ru.tensor.sbis.design.radio_group.control.api

import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupItem
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupValidationStatus
import ru.tensor.sbis.design.radio_group.SbisRadioGroupView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.Orientation

typealias SbisRadioGroupTitlePosition = HorizontalPosition
typealias SbisRadioGroupOrientation = Orientation

/**
 * Api компонента [SbisRadioGroupView].
 *
 * @author ps.smirnyh
 */
interface SbisRadioGroupViewApi {

    /** Список моделей радиокнопок. На их основе будут созданы вью радиокнопок. */
    var items: List<SbisRadioGroupItem>

    /** Позиция контента относительно маркера выбора радиокнопки. */
    var titlePosition: SbisRadioGroupTitlePosition

    /** Перенос радиокнопок, если они не помещаются по ширине при [orientation] равной [Orientation.HORIZONTAL]. */
    var multiline: Boolean

    /** Режим только для чтения. Блокирует взаимодействие пользователя с компонентом. */
    var readOnly: Boolean

    /** Id выбранной радиокнопки. По умолчанию равен [NO_ID]. */
    var selectedKey: String

    /** Расположение радиокнопок внутри группы. */
    var orientation: SbisRadioGroupOrientation

    /**
     * Отображение валидации радиогруппы.
     * В состоянии [SbisRadioGroupValidationStatus.INVALID] вокруг компонента будет нарисована красная рамка.
     */
    var validationStatus: SbisRadioGroupValidationStatus

    /** Callback на изменение [selectedKey]. */
    var onSelectedKeyChanged: ((String) -> Unit)?
}

/** Значение [SbisRadioGroupViewApi.selectedKey], когда нет выбранной радиокнопки. */
const val NO_ID = ""