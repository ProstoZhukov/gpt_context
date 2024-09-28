package ru.tensor.sbis.design_selection.contract.data

import android.os.Parcelable
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.persons.PersonName

/**
 * Базовый элемент в компоненте выбора.
 *
 * @property id ID элемента.
 * @property title Заголовок элемента.
 * @property subtitle Подзаголовок элемента.
 * @property titleHighlights список подсветок для заголовка.
 * @property photoData Данные, отображаемые в качестве аватара элемента.
 *
 * @author vv.chekurda
 */
interface SelectionItem : Parcelable {
    val id: SelectionItemId
    val title: String
    val subtitle: String?
    val titleHighlights: List<SearchSpan>
    val photoData: PhotoData? get() = null
    val clearedHighlights: SelectionItem get() = this
}

/**
 * Базовый элемент персоны в компоненте выбора.
 *
 * @property personName имя персоны.
 * @property isInMyCompany true, если из моей компании.
 * @property position Должность. null если чистый физик.
 */
interface SelectionPersonItem : SelectionItem {
    override val photoData: PersonData
    val personName: PersonName
    val isInMyCompany: Boolean
    val position: String?
}

/**
 * Базовый элемент папки в компоненте выбора.
 *
 * @property openable true, если в папку можно провалиться.
 * @property selectable true, если папку можно выбрать.
 * @property counter счетчик элементов в папке.
 */
interface SelectionFolderItem : SelectionItem {
    val openable: Boolean
    val selectable: Boolean
    val counter: Int? get() = null
}