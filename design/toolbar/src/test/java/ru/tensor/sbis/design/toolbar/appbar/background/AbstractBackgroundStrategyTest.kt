package ru.tensor.sbis.design.toolbar.appbar.background

import android.view.View
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import ru.tensor.sbis.design.toolbar.appbar.model.BackgroundModel
import ru.tensor.sbis.design.toolbar.appbar.model.ColorBackground
import ru.tensor.sbis.design.toolbar.appbar.model.ImageBackground
import ru.tensor.sbis.design.toolbar.appbar.model.UndefinedBackground
import kotlin.random.Random

/**
 * Тестирование базового функционала стратегии установки фона
 *
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
class AbstractBackgroundStrategyTest {

    private val view: View = mock()
    private val modelHandler: (BackgroundModel) -> Unit = mock()

    private val strategy: BackgroundStrategy = object : AbstractBackgroundStrategy<View>(view) {
        override fun setImageBackground(model: ImageBackground) = modelHandler(model)
    }

    @Test
    fun `Clear background test`() {
        strategy.setModel(UndefinedBackground)

        verify(view, only()).setBackgroundResource(0)
        verifyNoMoreInteractions(modelHandler)
    }

    @Test
    fun `Set color background test`() {
        val model = ColorBackground(Random.nextInt())

        strategy.setModel(model)

        verify(view, only()).setBackgroundColor(model.color)
    }

    @Test
    fun `Apply image model test`() {
        val model: ImageBackground = mock()

        strategy.setModel(model)

        verify(modelHandler, only()).invoke(model)
    }
}