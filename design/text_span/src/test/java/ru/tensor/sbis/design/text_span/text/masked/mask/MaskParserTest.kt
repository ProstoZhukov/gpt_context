package ru.tensor.sbis.design.text_span.text.masked.mask

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Тест корректности распознования маски.
 */
@RunWith(JUnitParamsRunner::class)
class MaskParserTest {

    private fun testMap() = arrayOf(
        arrayOf("*##############################", "types = *##############################, placeholders:[]"),
        arrayOf("*# ##", "types = *###, placeholders:[(2:` `]"),
        arrayOf("*# (###) ", "types = *####, placeholders:[(2:` (`, (5:`) `]"),
        arrayOf("*# (###) ###-##-##", "types = *###########, placeholders:[(2:` (`, (5:`) `, (8:`-`, (10:`-`]"),
        arrayOf("*# ", "types = *#, placeholders:[(2:` `]"),
        arrayOf("# ##", "types = ###, placeholders:[(1:` `]"),
        arrayOf("# (###) ", "types = ####, placeholders:[(1:` (`, (4:`) `]"),
        arrayOf("# (###) ###-##-##", "types = ###########, placeholders:[(1:` (`, (4:`) `, (7:`-`, (9:`-`]"),
        arrayOf("# ", "types = #, placeholders:[(1:` `]"),
        arrayOf("##-##", "types = ####, placeholders:[(2:`-`]"),
        arrayOf("#-##-##", "types = #####, placeholders:[(1:`-`, (3:`-`]"),
        arrayOf("##-##-##", "types = ######, placeholders:[(2:`-`, (4:`-`]"),
        arrayOf("###-##-##", "types = #######, placeholders:[(3:`-`, (5:`-`]"),
        arrayOf("(###) ###-##-##", "types = ##########, placeholders:[(0:`(`, (3:`) `, (6:`-`, (8:`-`]"),
        arrayOf("(AAA) ***-??-##", "types = AAA***??##, placeholders:[(0:`(`, (3:`) `, (6:`-`, (8:`-`]")
    )

    @Test
    @Parameters(method = "testMap")
    fun `парсер правильно опознает строку`(input: String, expected: String) {
        val parcer = MaskParser(input)
        assert(parcer.toString() == expected) {
            "Ошибка распознавания маски '%s' : ожидание %s реальность %s".format(input, expected, parcer.toString())
        }
    }
}