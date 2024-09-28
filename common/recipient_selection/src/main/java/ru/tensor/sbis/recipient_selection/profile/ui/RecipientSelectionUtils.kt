package ru.tensor.sbis.recipient_selection.profile.ui

import android.content.Context
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.profiles.generated.PersonDecoration
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionComponentProvider
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionComponent

/**
 * Расширения и встраиваемые функции, упрощающие работу с компонентом выбора получателей
 */
internal fun Context.getRecipientSelectionComponent(filter: RecipientsSearchFilter): RecipientSelectionComponent =
    RecipientSelectionComponentProvider.getRecipientSelectionComponent(this, filter)

internal inline fun <reified T> Any.castTo(): T? = this as? T

internal fun PersonDecoration?.mapPersonDecorationToInitialsStubData(): InitialsStubData? =
    this?.let {
        InitialsStubData(initials, backgroundColorHex)
    }