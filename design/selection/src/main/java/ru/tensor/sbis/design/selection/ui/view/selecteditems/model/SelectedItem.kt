package ru.tensor.sbis.design.selection.ui.view.selecteditems.model

import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId

/**
 * Набор типов моделей выбранных элементов, доступных для отображения
 *
 * @author us.bessonov
 */
sealed class SelectedItem {
    abstract val id: SelectorItemId
    lateinit var onClick: () -> Unit
}

/**
 * Простой текстовый элемент
 *
 * @author us.bessonov
 */
data class SelectedTextItem(override val id: SelectorItemId, val text: String) : SelectedItem()

/**
 * Элемент с картинкой и текстом
 *
 * @author us.bessonov
 */
data class SelectedItemWithImage(
    override val id: SelectorItemId,
    val imageUrl: String,
    val text: String
) : SelectedItem()

/**
 * Элемент с иконкой и названием папки
 *
 * @author us.bessonov
 */
data class SelectedFolderItem(override val id: SelectorItemId, val name: String) : SelectedItem()

/**
 * Элемент с изображением, задаваемым в [CompanyData], и заголовком
 *
 * @author us.bessonov
 */
data class SelectedCompanyItem(
    override val id: SelectorItemId,
    val title: String,
    val companyData: CompanyData
) : SelectedItem()

/**
 * Элемент с фото и именем персоны
 *
 * @author us.bessonov
 */
data class SelectedPersonItem(
    override val id: SelectorItemId,
    val photoData: PersonData,
    val firstName: String,
    val lastName: String
) : SelectedItem()