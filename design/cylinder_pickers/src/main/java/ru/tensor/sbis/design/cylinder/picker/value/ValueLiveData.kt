package ru.tensor.sbis.design.cylinder.picker.value

import io.reactivex.Observable
import ru.tensor.sbis.design.cylinder.picker.cylinder.IBindCylinder

/**
 * Интейфейс для инициализации значений в [CylinderLoopValuePicker].
 *
 * @author ae.noskov
 */
interface ValueLiveData<TYPE> {

    /** Id view пикера */
    var cylinder: Int

    /** @SelfDocumented */
    val valueChangeObservable: Observable<TYPE>

    /** Сеттер значения. */
    var valueSetter: TYPE

    /** Список значений. */
    val values: Collection<TYPE>

    /** @SelfDocumented */
    val comparator: Comparator<TYPE>

    /** @SelfDocumented */
    val collectionChangeObservable: Observable<List<TYPE>>

    /** @SelfDocumented */
    val bind: (IBindCylinder, TYPE) -> Unit
}