package ru.tensor.sbis.communication_decl.selection.recipient.data

import java.util.UUID

/**
 * Данные результата компонента выбора получателей.
 *
 * @property recipients список всех выбранных получателей, включает подразделения, персон, сохраняя порядок выбора.
 * @property allPersons список всех выбранных персон, включая тех, кто в выбранных подразделениях.
 * @property appended true, если элементы были выбраны посредством (+).
 *
 * @author vv.chekurda
 */
data class RecipientSelectionData(
    val recipients: List<RecipientSelectionItem> = emptyList(),
    val allPersons: List<RecipientPerson> = emptyList(),
    val appended: Boolean = false
) {

    /**
     * Получить список выбранных персон, исключая тех, кто находится в выбранных подразделениях.
     */
    val persons: List<RecipientPerson>
        get() = recipients.filterIsInstance(RecipientPerson::class.java)

    /**
     * Получить список выбранных подразделений.
     */
    val departments: List<RecipientDepartment>
        get() = recipients.filterIsInstance(RecipientDepartment::class.java)

    /**
     * Получить список идентификаторов всех выбранных элементов [recipients].
     */
    val uuids: List<UUID>
        get() = recipients.map { it.uuid }

    /**
     * Получить идентификаторы выбранных персон, включая тех, кто в выбранных папках.
     */
    val allPersonsUuids: List<UUID>
        get() = allPersons.map { it.uuid }

    /**
     * Получить идентификаторы выбранных персон, исключая тех, кто находится в выбранных подразделениях.
     */
    val personsUuids: List<UUID>
        get() = persons.map { it.uuid }

    /**
     * Получить идентификаторы выбранных подразделений.
     */
    val departmentsUuids: List<UUID>
        get() = departments.map { it.uuid }
}