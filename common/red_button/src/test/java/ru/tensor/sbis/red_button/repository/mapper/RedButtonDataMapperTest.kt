package ru.tensor.sbis.red_button.repository.mapper

import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.red_button_service.generated.RedButtonOnData

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonDataMapperTest {

    private val mapper = RedButtonDataMapper()

    @Test
    fun `When data is mapped, then all fields equals`() {
        //act
        val uuid = "uuid"
        val phoneNumber = "phone"
        val pinCode = "pin"
        val result = mapper.apply(RedButtonOnData(uuid, "", pinCode, phoneNumber))
        //verify
        assertEquals(uuid, result.operationUuid)
        assertEquals(phoneNumber, result.phone)
        assertEquals(pinCode, result.pin)
    }
}