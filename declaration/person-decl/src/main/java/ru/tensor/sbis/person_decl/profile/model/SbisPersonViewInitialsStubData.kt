package ru.tensor.sbis.person_decl.profile.model

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 * Данные для отображения заглушки с инициалами.
 * Дублёр класса `InitialsStubData` из `design_profile` для использования только в [SbisPersonViewData]
 *
 * @author us.bessonov
 */
data class SbisPersonViewInitialsStubData @JvmOverloads constructor(
    val initials: String,
    @ColorInt val initialsBackgroundColor: Int,
    @ColorRes val initialsBackgroundColorRes: Int = 0
)