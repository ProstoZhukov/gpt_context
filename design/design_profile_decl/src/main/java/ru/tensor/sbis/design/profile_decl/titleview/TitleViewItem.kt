package ru.tensor.sbis.design.profile_decl.titleview

import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Элемент, отображаемый в `SbisTitleView`. Может содержать часть составного заголовка, подзаголовка и фото для коллажа.
 *
 * @author us.bessonov
 */
data class TitleViewItem(
    val photoData: PhotoData = PersonData(),
    val title: CharSequence = "",
    val subtitle: CharSequence = ""
)