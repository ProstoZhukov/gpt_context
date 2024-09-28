package ru.tensor.sbis.tasks.feature

import android.os.Parcelable

/**
 * Маркер для ключа набора источников для карточки документа.
 * С помощью него можно выбирать правильный набор источников данных для карточки, использовать
 * правильную логику в особых местах.
 *
 * @author aa.sviridov
 */
abstract class SourceSetKey : Parcelable {

    /**
     * Ключ набора источников данных.
     */
    abstract val moduleKey: String
}