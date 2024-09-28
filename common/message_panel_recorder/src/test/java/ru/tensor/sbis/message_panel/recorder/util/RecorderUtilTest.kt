package ru.tensor.sbis.message_panel.recorder.util

import android.net.Uri
import org.mockito.kotlin.*
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Тесты вспомогательных инструментов
 *
 * @author vv.chekurda
 * Создан 8/8/2019
 */
@RunWith(JUnitParamsRunner::class)
class RecorderUtilTest {

    @Test(expected = IllegalArgumentException::class)
    // Схемы для примера. Список можно расширять по необходимости
    @Parameters("content", "http", "https", "maps", "map")
    fun `Create audio attachment failed on unsupported uri scheme`(scheme: String) {
        val uri = mock<Uri>().apply {
            whenever(this.scheme).thenReturn(scheme)
        }
        createAudioAttachment(uri)
    }
}