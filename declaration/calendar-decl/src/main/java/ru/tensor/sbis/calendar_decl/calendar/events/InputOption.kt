package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Опция создания события
 * @property key Ключ для идентификации опции
 * @property optionTitle Заголовок опции
 * @property optionType Тип опции
 */
@Parcelize
class InputOption(
    var key: String,
    var optionTitle: String,
    var optionType: InputOptionType,
) : Parcelable