package ru.tensor.sbis.edo_decl.passage.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Данные для перехода
 * В будущем здесь могут появиться вложения
 *
 * @property comment Комментарий к переходу
 *
 * @author sa.nikitin
 */
@Parcelize
data class PassageData(val comment: String = "") : Parcelable