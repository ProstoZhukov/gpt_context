package ru.tensor.sbis.design_dialogs.multipicker

import android.annotation.SuppressLint
import android.content.Context
import android.widget.NumberPicker
import org.apache.commons.lang3.reflect.FieldUtils
import timber.log.Timber

@SuppressLint("ViewConstructor")
class MultiPicker(context: Context) : NumberPicker(context) {

    init {
        try {
            // удаление линий разделителя
            FieldUtils.getField(NumberPicker::class.java, "mSelectionDivider", true).set(this, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}