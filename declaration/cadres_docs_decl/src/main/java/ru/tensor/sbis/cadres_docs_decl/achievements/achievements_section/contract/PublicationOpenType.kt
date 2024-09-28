package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Перечисление режимов работы экрана публикации ПиВ.
 */
@Parcelize
enum class PublicationOpenType : Parcelable {
    /** Изменение настроек и обновление документа. */
    UPDATE_DOCUMENT,
    /** Только изменение настроек. */
    CHANGE_SETTINGS_ONLY
}