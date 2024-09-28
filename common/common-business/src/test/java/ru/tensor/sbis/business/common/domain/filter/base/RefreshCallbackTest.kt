package ru.tensor.sbis.business.common.domain.filter.base

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import ru.tensor.sbis.business.common.testUtils.assertType
import ru.tensor.sbis.business.common.ui.base.Error

class RefreshCallbackTest {

    @Test
    fun `IsSuccess false with empty map`() {
        assertFalse(RefreshCallback(emptyMap()).isSuccess)
    }

    @Test
    fun `No error if callback with empty message`() {
        val error = RefreshCallback(emptyMap()).error

        assertNull(error)
    }

    @Test
    fun `ErrorMessage is empty with empty map`() {
        assertEquals("", RefreshCallback(emptyMap()).errorMessage)
    }

    @Test
    fun `IsFail true if has Result but has not Error`() {
        val map = mapOf("Result" to "2")
        val callback = RefreshCallback(map)

        assertFalse(callback.isSuccess)
    }

    @Test
    fun `IsSuccess false if has Result and has Error`() {
        val map = mapOf("Result" to "2", "Error" to "3")
        val callback = RefreshCallback(map)

        assertFalse(callback.isSuccess)
    }

    @Test
    fun `IsSuccess false if hasn't Result and has Error`() {
        val map = mapOf("Error" to "3")
        val callback = RefreshCallback(map)

        assertFalse(callback.isSuccess)
    }

    @Test
    fun `Error is PermissionError if code is 1`() {
        val map = mapOf("Error" to "1")
        val callback = RefreshCallback(map)

        assertType<Error.NoPermissionsError>(callback.error)
    }

    @Test
    fun `Error is PermissionError if code is 4`() {
        val map = mapOf("Error" to "4")
        val callback = RefreshCallback(map)

        assertType<Error.NoPermissionsError>(callback.error)
    }

    @Test
    fun `Error is UnknownError if code is not 1 or 4`() {
        val map = mapOf("Error" to "3")
        val callback = RefreshCallback(map)

        assertType<Error.UnknownError>(callback.error)
    }

    @Test
    fun `Error is NoInternetConnection if code is 0`() {
        val map = mapOf("Error" to "0")
        val callback = RefreshCallback(map)

        assertType<Error.NoInternetConnection>(callback.error)
    }

    @Test
    fun `ErrorMessage from ErrorMsg`() {
        val message = "message"
        val map = mapOf("ErrorMsg" to message)
        val callback = RefreshCallback(map)

        assertEquals(message, callback.errorMessage)
    }

    @Test
    fun `ErrorMessage from Error_message`() {
        val message = "message"
        val map = mapOf("Error_message" to message)
        val callback = RefreshCallback(map)

        assertEquals(message, callback.errorMessage)
    }
}