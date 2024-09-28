package ru.tensor.sbis.business.common.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharSequenceExtTest {

    @Test
    fun `containsAny CharSequence vararg true`() {
        assertTrue("".containsAny(""))
        assertTrue("11".containsAny(""))
        assertTrue("11".containsAny("1"))
        assertTrue("11".containsAny("11", "22", "33"))
        assertTrue("22".containsAny("11", "22", "33"))
        assertTrue("33".containsAny("11", "22", "33"))
        assertTrue("112233".containsAny("11", "22", "33"))
        assertTrue("112233".containsAny("12", "23"))
        assertTrue("114455".containsAny("11", "22", "33"))
    }

    @Test
    fun `containsAny CharSequence vararg false`() {
        assertFalse("".containsAny())
        assertFalse("".containsAny("1", "2", "3"))
        assertFalse("112233".containsAny("13", "24", "35"))
        assertFalse("112233".containsAny("7"))
    }

    @Test
    fun `containsAll CharSequence vararg true`() {
        assertTrue("".containsAll())
        assertTrue("".containsAll(""))
        assertTrue("112233".containsAll("11", "22", "33"))
        assertTrue("112233".containsAll("11", "22", "33", "12", "23"))
    }

    @Test
    fun `containsAll CharSequence vararg false`() {
        assertFalse("112233".containsAll("11", "22", "33", "44"))
        assertFalse("112233".containsAll("555"))
        assertFalse("12".containsAll("1", "2", "21"))
    }

    @Test
    fun `containsAny CharSequence collection true`() {
        assertTrue("".containsAny(listOf("")))
        assertTrue("11".containsAny(listOf("")))
        assertTrue("11".containsAny(listOf("1")))
        assertTrue("11".containsAny(listOf("11", "22", "33")))
        assertTrue("22".containsAny(listOf("11", "22", "33")))
        assertTrue("33".containsAny(listOf("11", "22", "33")))
        assertTrue("112233".containsAny(listOf("11", "22", "33")))
        assertTrue("112233".containsAny(listOf("12", "23")))
        assertTrue("114455".containsAny(listOf("11", "22", "33")))
    }

    @Test
    fun `containsAny CharSequence collection false`() {
        assertFalse("".containsAny(emptyList()))
        assertFalse("".containsAny(listOf("1", "2", "3")))
        assertFalse("112233".containsAny(listOf("13", "24", "35")))
        assertFalse("112233".containsAny(listOf("7")))
    }

    @Test
    fun `containsAll CharSequence collection true`() {
        assertTrue("".containsAll(emptyList()))
        assertTrue("".containsAll(listOf("")))
        assertTrue("112233".containsAll(listOf("11", "22", "33")))
        assertTrue("112233".containsAll(listOf("11", "22", "33", "12", "23")))
    }

    @Test
    fun `containsAll CharSequence collection false`() {
        assertFalse("112233".containsAll(listOf("11", "22", "33", "44")))
        assertFalse("112233".containsAll(listOf("555")))
        assertFalse("12".containsAll(listOf("1", "2", "21")))
    }
}
