package ru.tensor.sbis.design.navigation.view

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.inOrder
import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class HideableNavigationViewTest {

    @Spy
    private lateinit var spyHideableNavigationView: TestHideableNavigationViewImpl

    @Test
    fun `When hideAndLock method invoked, then it is hidden without animation and then pinned`() {
        spyHideableNavigationView.hideAndLock()

        inOrder(spyHideableNavigationView).apply {
            verify(spyHideableNavigationView).hide(false)
            verify(spyHideableNavigationView).pinned = true
        }
    }

    @Test
    fun `When showAndUnlock method invoked, then it is unpinned and then shown without animation`() {
        spyHideableNavigationView.showAndUnlock()

        inOrder(spyHideableNavigationView).apply {
            verify(spyHideableNavigationView).pinned = false
            verify(spyHideableNavigationView).show(false)
        }
    }

}

private class TestHideableNavigationViewImpl : HideableNavigationView {
    override var pinned: Boolean = false

    override fun hide(animated: Boolean) = Unit

    override fun show(animated: Boolean) = Unit
}