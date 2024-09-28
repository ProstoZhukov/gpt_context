package ru.tensor.sbis.message_panel.recorder.util

import android.net.Uri
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Тесты вспомогательных инструментов, которые зависят от Android
 *
 * @author vv.chekurda
 * Создан 8/8/2019
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class RecorderUtilAndroidDependTest {

    @Test
    // Тест для визуальной проверки отсутствия проблем с кодировками
    fun `Create audio attachment from cyrillic file`() {
        val fileName = "Аудиосообщение.mp3"
        val path = "/path/to/"
        val uri = Uri.parse("file:" + File(path, fileName).invariantSeparatorsPath)

        val attachment = createAudioAttachment(uri)

        assertThat(attachment.uri, equalTo("file:$path$fileName"))
        assertThat(attachment.name, equalTo(fileName))
    }
}