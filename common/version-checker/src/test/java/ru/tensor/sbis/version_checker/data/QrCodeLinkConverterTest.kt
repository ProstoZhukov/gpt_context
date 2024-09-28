package ru.tensor.sbis.version_checker.data

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.common.BuildConfig

internal class QrCodeLinkConverterTest {

    private val qrCodeLinkConverter = QrCodeLinkConverter()

    @Test
    fun `Given correct qr string with app suffix, when extract app suffix, then return app id (communicator)`() {
        val result = qrCodeLinkConverter.parse("https://online.sbis.ru/auth/qrcode/sbis/?token=QDKNJNVLKIKNNBFCKJDBVKJ")

        assertThat(result, containsString("ru.tensor.sbis.droid.saby"))
    }

    @Test
    fun `Given correct qr string with app suffix, when extract app suffix, then return app id (retail)`() {
        val result =
            qrCodeLinkConverter.parse("https://online.sbis.ru/auth/qrcode/sbisRetail/?token=QDKNJNVLKIKNNBFCKJDBVKJ")

        assertThat(result, containsString("ru.tensor.sbis.retail"))
    }

    @Test
    fun `Given incorrect qr string, then return null`() {
        val result =
            qrCodeLinkConverter.parse("https://online.sbis.ru/auth/qrcode/sbisNoApp/?token=QDKNJNVLKIKNNF23BF5CKJDBVKJ")

        assertNull(result)
    }

    @Test
    fun `Given incorrect qr string with no auth path, then return null`() {
        val result =
            qrCodeLinkConverter.parse("https://online.sbis.ru/qrcode/sbisRetail/?token=QDKNJNVLKIKNNBFCKJDBVKJ")

        assertNull(result)
    }

    @Test
    fun `Given incorrect qr string with no qrcode path, then return null`() {
        val result = qrCodeLinkConverter.parse("https://online.sbis.ru/auth/sbisRetail/?token=QDKNJNVLKIKNNBFCKJDBVKJ")

        assertNull(result)
    }

    @Test
    fun `Given result contains debug suffix`() {
        val result =
            qrCodeLinkConverter.parse("https://online.sbis.ru/auth/qrcode/sbisBusiness/?token=QDKNJNVLKIKNNBFCKJDBVKJ")

        if (BuildConfig.DEBUG) {
            assertThat(result, containsString(BuildConfig.BUILD_TYPE))
        } else {
            assertThat(result, not(containsString(BuildConfig.BUILD_TYPE)))
        }
    }
}