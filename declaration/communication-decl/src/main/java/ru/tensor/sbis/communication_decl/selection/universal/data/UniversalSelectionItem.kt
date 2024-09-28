package ru.tensor.sbis.communication_decl.selection.universal.data

import android.os.Parcelable

/**
 * Интерфейс элемента универсального выбора.
 *
 * @author vv.chekurda
 */
interface UniversalSelectionItem : Parcelable {

    /**
     * Идентификатор элемента.
     */
    val id: String

    /**
     * Заголовок элемента.
     */
    val title: String

    /**
     * Подзаголовок элемента.
     */
    val subtitle: String?

    /**
     * Признак того, что элемент является папкой.
     */
    val isFolder: Boolean
}