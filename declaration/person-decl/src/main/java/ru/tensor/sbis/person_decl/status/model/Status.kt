package ru.tensor.sbis.person_decl.status.model

import android.os.Parcelable

/**
 * Интерфейс описывающий статус пользователя
 */
interface Status : Parcelable {
    /**
     * @property privateId идентификатор статуса
     */
    val privateId: Int

    /**
     * @property iconName название иконки
     */
    val iconName: String?

    /**
     * @property iconSize размер иконки
     */
    val iconSize: StatusIconSize

    /**
     * @property iconColor цвет иконки
     */
    val iconColor: StatusIconColor

    /**
     * @property title название статуса
     */
    var title: String?

    /**
     * @property isWorking является ли статус рабочим
     */
    var isWorking: Boolean

    /**
     * @property isSystem является ли статус стандартным
     */
    var isSystem: Boolean

    /**
     * @property isFolder является ли статус папкой
     */
    var isFolder: Boolean

    /**
     * @property isCurrent является ли статус текущим
     */
    var isCurrent: Boolean

    /**
     * @property isEmojiIcon является ли иконка эмодзи
     */
    val isEmojiIcon: Boolean
}
