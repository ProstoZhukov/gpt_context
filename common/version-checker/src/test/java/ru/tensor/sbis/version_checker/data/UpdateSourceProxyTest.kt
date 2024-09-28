package ru.tensor.sbis.version_checker.data

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.version_checker_decl.data.UpdateSource

@RunWith(RobolectricTestRunner::class) // для работы Uri.parse()
@Config(sdk = [Build.VERSION_CODES.R])
internal class UpdateSourceProxyTest {

    @Test
    fun `All possible update source schemes were correctly resolved`() {
        getAppIdAnchor().forEach { appInfo ->
            val (appId, anchor) = appInfo

            UpdateSource.values().forEach { updateSource ->
                val proxy = UpdateSourceProxy(updateSource)
                when (updateSource) {
                    UpdateSource.SBIS_ONLINE -> {
                        val url = proxy.buildUrl(appId)
                        assertEquals("https://sbis.ru/apps#$anchor", url)
                    }
                    else -> {
                        val uri = proxy.buildUri(appId)
                        val scheme = getScheme(proxy)
                        assertEquals(scheme.replace("<package_name>", appId), uri?.toString())
                    }
                }
            }
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun getAppIdAnchor() = listOf(
        listOf("ru.tensor.sbis.droid", "sbis"),
        listOf("ru.tensor.sbis.droid.saby", "sbis"),
        listOf("ru.tensor.waiter", "waiter"),
        listOf("ru.tensor.sbis.waiter", "waiter"),
        listOf("ru.tensor.sbis.waiter.saby", "waiter"),
        listOf("ru.tensor.sbis.courier", "courier"),
        listOf("ru.tensor.sbis.courier.saby", "courier"),
        listOf("ru.tensor.sbis.retail_app", "cashbox"),
        listOf("ru.tensor.sbis.presto", "presto"),
        listOf("ru.tensor.sbis.appmarket", "sabyappmarket"),
        listOf("ru.tensor.sbis.business", "bussiness"),
        listOf("ru.tensor.cookscreen", "cookscreen"),
        listOf("ru.tensor.hallscreen", "hallscreen"),
        listOf("ru.tensor.showcase", "sabyget"),
        listOf("ru.tensor.sbis.sms", "sms"),
        listOf("ru.tensor.sbis.storekeeper", "docs"),
        listOf("ru.tensor.sbis.sabyadmin", "SbisSabyAdminMobile")
    )

    private fun getScheme(proxy: UpdateSourceProxy): String {
        val field = UpdateSourceProxy::class.java.getDeclaredField("scheme")
        field.isAccessible = true
        return field.get(proxy) as String
    }
}