package ru.tensor.sbis.business.common.utils

import android.graphics.Color
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ColorUtilsTest {

    @Test
    fun `toColor alpha component`() {
        assertEquals(0.1f, 0.colorAlpha(0.1f), 0.05f)
        assertEquals(0.5f, 0.colorAlpha(0.5f), 0.05f)
        assertEquals(0.7f, 0.colorAlpha(0.7f), 0.05f)
        assertEquals(0.9f, 0.colorAlpha(0.9f), 0.05f)
    }

    @Test
    fun `toColor alpha component edge cases`() {
        assertEquals(0.0f, 0.colorAlpha(0.0f))
        assertEquals(0.0f, 0.colorAlpha(-200.0f))
        assertEquals(1.0f, 0.colorAlpha(1.0f))
        assertEquals(1.0f, 0.colorAlpha(20.0f))
    }

    @Test
    fun `When color converter, then RGB components stay the same`() {
        checkColorComponentsAreSame(0xFF00BB, 0xFF00BB.toColor(0.0f))
        checkColorComponentsAreSame(0xFF00BB, 0xFF00BB.toColor(0.5f))
        checkColorComponentsAreSame(0xFF00BB, 0xFF00BB.toColor(0.1f))

        checkColorComponentsAreSame(0x000000, 0x000000.toColor(0.12f))
        checkColorComponentsAreSame(0xFFFFFF, 0xFFFFFF.toColor(0.12f))
    }

    /**
     * Конвертирует Int в цвет и возвращает альфу
     *
     * @param alpha альфа, которую нужно применить к цвету
     *
     * @return альфа компонент цвета [Color]
     */
    private fun Int.colorAlpha(alpha: Float): Float {
        val intColor = this.toColor(alpha)
        return Color.valueOf(intColor).alpha()
    }

    private fun checkColorComponentsAreSame(
        initialColor: Int,
        resultColor: Int
    ) {
        val color1 = Color.valueOf(initialColor)
        val color2 = Color.valueOf(resultColor)

        assertEquals(color1.red(), color2.red())
        assertEquals(color1.green(), color2.green())
        assertEquals(color1.blue(), color2.blue())
    }
}
