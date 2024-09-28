package ru.tensor.sbis.business.common.utils

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.business.common.ui.prefs.DEFAULT_STRING
import ru.tensor.sbis.business.common.ui.prefs.containsAll
import ru.tensor.sbis.business.common.ui.prefs.getStringOrDefault
import ru.tensor.sbis.business.common.ui.prefs.getStringSetOrDefault

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SharedPreferencesExtensionsTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val prefs = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)

    @After
    fun cleanup() = prefs.edit().clear().apply()

    @Test
    fun `getStringOrDefault() with default value`() {
        assertEquals(DEFAULT_STRING, prefs.getStringOrDefault("some_key"))
        assertEquals("value", prefs.getStringOrDefault("some_key", "value"))
    }

    @Test
    fun `getStringOrDefault() with saved string`() {
        val key = "key1"
        val value = "some value"

        prefs.edit().putString(key, value).apply()

        assertEquals(value, prefs.getStringOrDefault(key))
    }

    @Test
    fun `getStringSetOrDefault() with default value`() {
        assertEquals(emptySet<String>(), prefs.getStringSetOrDefault("some_key"))
        assertEquals(
            setOf("1", "2", "3"),
            prefs.getStringSetOrDefault("some_key", setOf("1", "2", "3"))
        )
    }

    @Test
    fun `getStringSetOrDefault() with saved string`() {
        val key = "key2"
        val value = setOf("a", "b", "c")

        prefs.edit().putStringSet(key, value).apply()

        assertEquals(value, prefs.getStringSetOrDefault(key))
    }

    @Test
    fun `containsAll true`() {
        prefs.edit {
            putString("k1", "str")
            putInt("k2", 2)
            putLong("k3", 3L)
        }

        assertTrue(prefs.containsAll("k1", "k2", "k3"))
    }

    @Test
    fun `containsAll false partial`() {
        prefs.edit {
            putString("k1", "str")
            putLong("k3", 3L)
        }

        assertFalse(prefs.containsAll("k1", "k2", "k3"))
    }

    @Test
    fun `containsAll false empty`() {
        prefs.edit {
            clear()
        }

        assertFalse(prefs.containsAll("k1", "k2", "k3"))
    }
}
