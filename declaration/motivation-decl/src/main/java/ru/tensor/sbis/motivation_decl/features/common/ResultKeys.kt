package ru.tensor.sbis.motivation_decl.features.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Класс с ключами для возврата результата с помощью setFragmentResult. */
@Parcelize
data class ResultKeys(val requestKey: String, val dataKey: String): Parcelable