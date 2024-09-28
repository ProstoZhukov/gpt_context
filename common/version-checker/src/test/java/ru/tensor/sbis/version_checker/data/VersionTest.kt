package ru.tensor.sbis.version_checker.data

import org.mockito.kotlin.doAnswer
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import ru.tensor.sbis.common.testing.mockStatic
import timber.log.Timber
import timber.log.Timber.e

internal class VersionTest {

    private val currentVersion = Version("18.23.100")

    @Test
    fun `Current version equals to itself`() {
        assertTrue(currentVersion == Version("18.23.100"))
        assertTrue(Version("18.23") == Version("18.23.0"))
    }

    @Test
    fun `Check major version`() {
        assertFalse(Version("16.30.300") >= currentVersion)
        assertTrue(Version("20.22.0") >= currentVersion)
        assertTrue(Version("17.22.0") < currentVersion)
        assertFalse(Version("19.22.0") < currentVersion)
    }

    @Test
    fun `Check minor version`() {
        assertFalse(Version("18.22.200") >= currentVersion)
        assertTrue(Version("18.24.100") >= currentVersion)
        assertTrue(Version("18.100.100") >= currentVersion)
        assertTrue(Version("18.19.5") < currentVersion)
        assertFalse(Version("18.23.100") < currentVersion)
        assertTrue(Version("18.0.100") < currentVersion)
    }

    @Test
    fun `Check patch version`() {
        assertFalse(Version("18.23.0") >= currentVersion)
        assertFalse(Version("18.23.99") >= currentVersion)
        assertTrue(Version("18.23.200") >= currentVersion)
        assertTrue(Version("18.23.99") < currentVersion)
        assertFalse(Version("18.23.100") < currentVersion)
        assertFalse(Version("18.23.101") < currentVersion)
    }

    @Test
    fun `Check that version is NOT specified`() {
        assertTrue(Version("0.0").isUnspecified)
        assertTrue(Version("0.0.0").isUnspecified)
        assertTrue(Version("0.0.0.0").isUnspecified)
    }

    @Test
    fun `Check that version is NOT specified if it has only cpp segment`() {
        assertTrue(Version("0.0.0.123").isUnspecified)
    }

    @Test
    fun `Check that version IS specified`() {
        assertFalse(currentVersion.isUnspecified)
        assertFalse(Version("0.1").isUnspecified)
        assertFalse(Version("0.1.0").isUnspecified)
        assertFalse(Version("0.0.1").isUnspecified)
        assertFalse(Version("0.0.010").isUnspecified)
        assertFalse(Version("1.0.0").isUnspecified)
        assertFalse(Version("1.0.0.0").isUnspecified)
        assertFalse(Version("1.0.1.0").isUnspecified)
    }

    @Test
    fun `Log error on empty or invalid version`() {
        var errorWasLogged = false
        mockStatic<Timber> {
            on<Unit> { e(anyString()) } doAnswer { errorWasLogged = true }
        }

        Version("")
        assertTrue(errorWasLogged)

        errorWasLogged = false
        Version("abc")
        assertTrue(errorWasLogged)
    }

    @Test
    fun `Hashcode was correctly built`() {
        val version = Version("21.30.100")
        val expectedHashCode = HashCodeBuilder().append(version.version).append(".").toHashCode()
        val actualHashCode = version.hashCode()
        assertEquals(expectedHashCode, actualHashCode)
    }
}