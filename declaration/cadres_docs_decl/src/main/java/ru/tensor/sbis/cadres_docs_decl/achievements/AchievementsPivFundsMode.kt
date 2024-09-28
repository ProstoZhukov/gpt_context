package ru.tensor.sbis.cadres_docs_decl.achievements

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Перечисление режимов открытия фрагмента выбора фондов/ПИВов
 */
@Parcelize
enum class AchievementsPivFundsMode: Parcelable {
    PIV,
    FUNDS
}