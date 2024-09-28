package ru.tensor.sbis.design_dialogs.movablepanel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R as RDesign

@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
internal class MovablePanelTest {

    companion object {
        val MAX_PEEK_HEIGHT = MovablePanelPeekHeight.Percent(1F)
        val FIX_MAX_PEEK_HEIGHT = MovablePanelPeekHeight.Percent(.67F)
        val HIDDEN_PEEK_HEIGHT = MovablePanelPeekHeight.Percent(0F)
    }

    private val initListValue =
        listOf(FIX_MAX_PEEK_HEIGHT, MAX_PEEK_HEIGHT, HIDDEN_PEEK_HEIGHT, MAX_PEEK_HEIGHT, MAX_PEEK_HEIGHT)

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val movablePanel by lazy { MovablePanel(context) }

    @Before
    fun setUp() {
        context.theme.applyStyle(RDesign.style.BaseAppTheme, false)
    }

    /**
     * Проверка на то, что все контейнеры успешно объявлены
     */
    @Test
    fun `When init MovablePanel then has not null containers and behavior`() {
        Assert.assertNotNull(movablePanel.contentContainer)
        Assert.assertNotNull(movablePanel.contentRootContainer)
        Assert.assertNotNull(movablePanel.movablePanelContainer)
    }

    /**
     * Проверка на то, что behavior установлен
     */
    @Test
    fun `When init MovablePanel then has behavior`() {
        Assert.assertNotNull(movablePanel.behavior)
    }

    /**
     * Проверка на то, что установили корректный список состояний шторки (не больше 4), иначе получаем эксепшн
     */
    @Test(expected = IllegalArgumentException::class)
    fun `When set more than 4 states then get exception`() {
        movablePanel.setPeekHeightList(initListValue, MAX_PEEK_HEIGHT)
    }

    /**
     * Проверка на то, что установили корректный список состояний шторки (не меньше 2), иначе получаем эксепшн
     */
    @Test(expected = IllegalArgumentException::class)
    fun `When set less than 2 states then get exception`() {
        val initList = listOf(MAX_PEEK_HEIGHT)
        movablePanel.setPeekHeightList(initList, MAX_PEEK_HEIGHT)
    }

    /**
     * Проверка на то, что корректно устанавливается состояние шторки
     */
    @Test
    fun `When set peekHeight value then behavior has correct fun getPeekHeight`() {
        movablePanel.peekHeight = MovablePanelPeekHeight.Percent(1F)
        assertEquals(movablePanel.behavior?.getPeekHeight(), movablePanel.peekHeight)
    }
}