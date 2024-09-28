package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Описание для отрисовки фона дня, входящего в выбранный период.
 *
 * @property quantumType тип кванта.
 * @property drawableType тип отрисовки.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class QuantumSelection(
    val quantumType: QuantumType = QuantumType.NO_SELECTION,
    val drawableType: QuantumDrawableType = QuantumDrawableType.DefaultDrawableType
) : Parcelable