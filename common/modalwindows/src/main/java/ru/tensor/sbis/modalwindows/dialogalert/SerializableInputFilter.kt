package ru.tensor.sbis.modalwindows.dialogalert

import android.text.InputFilter
import java.io.Serializable

/**
 * Стандартный фильтр из android sdk [InputFilter],
 * реализующий [Serializable] интерфейс для возможности сохранять фильтры при saveState/restoreState
 */
abstract class SerializableInputFilter : InputFilter, Serializable