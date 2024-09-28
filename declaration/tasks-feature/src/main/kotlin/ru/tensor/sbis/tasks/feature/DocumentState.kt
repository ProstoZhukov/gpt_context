package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель состояния документа для облегчённой модели задачи, см. [DocumentMainDetails].
 *
 * @author aa.sviridov
 */
@Parcelize
enum class DocumentState : Parcelable {
    /**
     * Подтверждено.
     */
    CONFIRM,

    /**
     * Отклонено.
     */
    DECLINE
}