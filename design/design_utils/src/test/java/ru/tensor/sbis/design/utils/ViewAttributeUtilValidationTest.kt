package ru.tensor.sbis.design.utils

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Тестирование правил проверки параметров
 *
 * @author ma.kolpakov
 * Создан 9/28/2019
 */
@RunWith(JUnitParamsRunner::class)
class ViewAttributeUtilValidationTest {

    @Test
    @Parameters(method = "parametersForValidationTest")
    fun `Test attribute string validation`(attributeString: CharSequence, expected: Boolean) {
        assertThat(attributeString.isValidReferenceString, equalTo(expected))
    }

    private fun parametersForValidationTest(): List<List<Any>> = listOf(
        listOf("", false),
        listOf(" ", false),
        listOf("id1, id2", false),
        listOf("id1,id2 ", false),
        listOf(" id1,id2", false),
        listOf("id1,id2", true),
        listOf("_id1,id_2,id3_", true)
    )
}