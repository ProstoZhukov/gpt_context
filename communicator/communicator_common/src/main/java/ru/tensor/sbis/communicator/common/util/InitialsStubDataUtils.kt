package ru.tensor.sbis.communicator.common.util

import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.profiles.generated.PersonDecoration

/**
 * Преобразования PersonDecoration в InitialsStubData
 */
fun PersonDecoration?.mapPersonDecorationToInitialsStubData(): InitialsStubData? =
    this?.let {
        InitialsStubData(initials, backgroundColorHex)
    }

/**
 * Преобразования InitialsStubData в PersonDecoration
 */
fun InitialsStubData?.mapInitialsStubDataToPersonDecoration(): PersonDecoration? =
    this?.let {
        PersonDecoration(
            initials,
            Integer.toHexString(initialsBackgroundColor)
        )
    }