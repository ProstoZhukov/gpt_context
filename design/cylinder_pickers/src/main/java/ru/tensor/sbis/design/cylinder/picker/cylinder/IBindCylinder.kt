package ru.tensor.sbis.design.cylinder.picker.cylinder

/**
 * Интерфейс для биндинга ViewHolder в [ru.tensor.sbis.design.cylinder.picker.value.CylinderLoopValuePicker].
 *
 * @author ae.noskov
 */
interface IBindCylinder {

    /** @SelfDocumented */
    fun bind(text: String)

    /** @SelfDocumented */
    fun bind(text: String, color: Int)
}