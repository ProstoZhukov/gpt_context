package ru.tensor.sbis.design.profile.person.data

import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData

private const val BLANK_STRING = "    "
private const val FIRST_NAME = "John"
private const val LAST_NAME = "Smith"

/**
 * @author ma.kolpakov
 */
class InitialsStubDataTest {

    @Test
    fun `When last name and first name are present, then initials contains first characters`() {
        val stubData = InitialsStubData.createByNameParts(LAST_NAME, FIRST_NAME)!!

        assertEquals("SJ", stubData.initials)
    }

    @Test
    fun `When last name is empty, then initials contains first two characters from first name`() {
        val stubData = InitialsStubData.createByNameParts("", FIRST_NAME)!!

        assertEquals("Jo", stubData.initials)
    }

    @Test
    fun `When first name is empty, then initials contains first two characters from last name`() {
        val stubData = InitialsStubData.createByNameParts(LAST_NAME, "")!!

        assertEquals("Sm", stubData.initials)
    }

    @Test
    fun `When last name is blank, then initials contains first two characters from first name`() {
        val stubData = InitialsStubData.createByNameParts(BLANK_STRING, FIRST_NAME)!!

        assertEquals("Jo", stubData.initials)
    }

    @Test
    fun `When first name is blank, then initials contains first two characters from last name`() {
        val stubData = InitialsStubData.createByNameParts(LAST_NAME, BLANK_STRING)!!

        assertEquals("Sm", stubData.initials)
    }

    @Test
    fun `When last name and first name contains blank characters at the beginning, then initials contains first characters`() {
        val stubData = InitialsStubData.createByNameParts("$BLANK_STRING$LAST_NAME", "$BLANK_STRING$FIRST_NAME")!!

        assertEquals("SJ", stubData.initials)
    }

    @Test
    fun `When last name and first name both are absent, then initials should not be returned`() {
        val stubData = InitialsStubData.createByNameParts("", "")

        assertNull(stubData)
    }

    @Test
    fun `When last name and first name both are blank, then initials should not be returned`() {
        val stubData = InitialsStubData.createByNameParts(BLANK_STRING, BLANK_STRING)

        assertNull(stubData)
    }
}