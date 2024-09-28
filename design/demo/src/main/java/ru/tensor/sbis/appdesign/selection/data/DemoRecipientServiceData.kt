package ru.tensor.sbis.appdesign.selection.data

import ru.tensor.sbis.design.profile.person.data.PersonData
import java.util.*

/**
 * @author ma.kolpakov
 */
data class DemoRecipientServiceData(
    val id: UUID,
    val title: String,
    val subtitle: String?,
    val recipientType: RecipientType,
    val imageUrl: String? = null,
    val membersCount: Int = 0,
    val personData: PersonData? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

enum class RecipientType {
    PERSON,
    GROUP,
    DEPARTMENT
}