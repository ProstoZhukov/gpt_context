package ru.tensor.sbis.design.profile.util

import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate

/** Тестовые данные - фамилия, имя и отчество, передаваемые в парсер */
typealias NameData = Triple<String?, String?, String?>

/** @SelfDocumented */
internal class PersonNameTemplateTest {

    private val surname = "Печкин"
    private val name = "Игорь"
    private val patronymic = "Евгеньевич"

    /** ФИО -> Ожидаемый результат парсинга */
    private val surnameNPTestData = listOf<Pair<NameData, String>>(
        Triple(surname, name, patronymic) to "Печкин И. Е.",
        Triple("", name, patronymic) to "И. Е.",
        Triple(null, name, patronymic) to "И. Е.",
        Triple(surname, "", patronymic) to "Печкин Е.",
        Triple(surname, null, patronymic) to "Печкин Е.",
        Triple(surname, name, "") to "Печкин И.",
        Triple(surname, name, null) to "Печкин И.",
        Triple("", "", "") to "",
        Triple(surname, "", "") to "Печкин",
        Triple(surname, null, null) to "Печкин",
        Triple("", name, "") to "И.",
        Triple(null, name, null) to "И.",
        Triple("", "", patronymic) to "Е.",
        Triple(null, null, patronymic) to "Е."
    )

    @Test
    fun `When SURNAME_N_P pattern used, format result match with expected result`() {
        surnameNPTestData.forEach { (nameData, result) ->
            val (surname, name, patronymic) = nameData
            Assert.assertEquals(PersonNameTemplate.SURNAME_N_P.format(surname, name, patronymic), result)
        }
    }
}