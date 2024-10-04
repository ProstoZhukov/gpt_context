package ru.tensor.sbis.design.whats_new.domain

import android.content.Context
import android.content.SharedPreferences
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.whats_new.SbisWhatsNewPlugin

/**
 * Тестовый класс для [ShowConditionManagerImpl].
 *
 * @author ps.smirnyh
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ShowConditionManagerImplTest {

    private val mockSharedPrefs: SharedPreferences = mockk()

    private var mockContext: Context = mockk()

    @Test
    fun `When app version has not changed, then don't show screen`() {
        val currentVersion = "22.1204"
        every { mockSharedPrefs.getString(any(), any()) } returns currentVersion
        createWhatsNewTextMock()
        val showConditionManager = createShowConditionManager(currentVersion)
        assert(showConditionManager.checkShowing())
    }

    @Test
    fun `When app version has changed, then show screen`() {
        val currentVersion = "22.1204"
        val oldVersion = "21.6262"
        every { mockSharedPrefs.getString(any(), any()) } returns oldVersion
        createWhatsNewTextMock()
        val showConditionManager = createShowConditionManager(currentVersion)
        assert(showConditionManager.checkShowing().not())
    }

    @Test
    fun `When app version has changed, but it bugfix, then don't show screen`() {
        val currentVersion = "22.1204.1"
        val oldVersion = "22.1204"
        every { mockSharedPrefs.getString(any(), any()) } returns oldVersion
        createWhatsNewTextMock()
        val showConditionManager = createShowConditionManager(currentVersion)
        assert(showConditionManager.checkShowing())
    }

    @Test
    fun `When text for 'whats new' is empty, then don't show screen`() {
        val currentVersion = "22.1204"
        val oldVersion = "22.0102"
        val editor: SharedPreferences.Editor = mockk()
        every { mockSharedPrefs.getString(any(), any()) } returns oldVersion
        every { mockSharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } just Runs
        createWhatsNewTextMock(text = "")
        val showConditionManager = createShowConditionManager(currentVersion)
        assert(showConditionManager.checkShowing())
    }

    private fun createShowConditionManager(versionApp: String) =
        ShowConditionManagerImpl(versionApp, mockContext, prefs = mockSharedPrefs)

    private fun createWhatsNewTextMock(text: String = "Mock text") =
        every { mockContext.getString(SbisWhatsNewPlugin.customizationOptions.whatsNewRes) } returns text
}