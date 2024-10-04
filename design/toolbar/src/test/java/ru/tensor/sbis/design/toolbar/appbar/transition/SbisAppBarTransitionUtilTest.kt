package ru.tensor.sbis.design.toolbar.appbar.transition

import android.content.Intent
import android.os.Bundle
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.model.AppBarModel

/**
 * @author ma.kolpakov
 * Создан 10/1/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SbisAppBarTransitionUtilTest {

    @Mock
    private lateinit var bundle: Bundle

    @Mock
    private lateinit var intent: Intent

    @Mock
    private lateinit var model: AppBarModel

    @Mock
    private lateinit var view: SbisAppBarLayout

    @Test
    fun `Save state to bundle`() {
        model.saveState(bundle)

        verifyNoMoreInteractions(model)
        verify(bundle, only()).putParcelable(any(), eq(model))
    }

    @Test
    fun `Save state to intent`() {
        model.saveState(intent)

        verifyNoMoreInteractions(model)
        verify(intent, only()).putExtra(any(), eq(model))
    }

    @Test
    fun `Save transition state to bundle`() {
        model.saveTransitionState(bundle)

        verify(model, only()).currentOffset = 0F
        verify(bundle, only()).putParcelable(any(), eq(model))
    }

    @Test
    fun `Save transition state to intent`() {
        model.saveTransitionState(intent)

        verify(model, only()).currentOffset = 0F
        verify(intent, only()).putExtra(any(), eq(model))
    }

    @Test
    fun `Restore state from bundle`() {
        whenever(bundle.getParcelable<AppBarModel>(any())).thenReturn(model)

        view.restoreState(bundle)

        verify(view, only()).model = model
        verify(bundle, only()).getParcelable<AppBarModel>(any())
    }

    @Test
    fun `Restore state from nullable bundle`() {
        view.restoreState(null as Bundle?)

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Restore state from intent`() {
        whenever(intent.extras).thenReturn(bundle)
        whenever(bundle.getParcelable<AppBarModel>(any())).thenReturn(model)

        view.restoreState(intent)

        verify(view, only()).model = model
        verify(intent, only()).extras
        verify(bundle, only()).getParcelable<AppBarModel>(any())
    }

    @Test
    fun `Restore state from nullable intent`() {
        view.restoreState(null as Intent?)

        verifyNoMoreInteractions(view)
    }
}