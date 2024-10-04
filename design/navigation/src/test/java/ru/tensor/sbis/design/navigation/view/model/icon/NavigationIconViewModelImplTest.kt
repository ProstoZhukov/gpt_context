package ru.tensor.sbis.design.navigation.view.model.icon

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.subjects.BehaviorSubject
import junitparams.JUnitParamsRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.navigation.view.model.*

private const val ICON_RES = 1
private const val ICON_SELECTED_RES = 10

/**
 * Все тесты нужно проводить без подписок, чтобы гарантировать доставку события после подписки
 *
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
internal class NavigationIconViewModelImplTest {

    private val defaultModel = NavigationItemIcon(ICON_RES, ICON_SELECTED_RES)

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val model = BehaviorSubject.createDefault(defaultModel)

    private val state = BehaviorSubject.create<NavigationItemState>()

    private lateinit var vm: NavigationIconViewModel

    @Test
    fun `When icon model visibility changed to false, then visibility should become equal to false`() {
        val defaultVisibility = defaultModel.isVisible
        val changedVisibility = defaultModel.isVisible.not()
        vm = NavigationIconViewModelImpl(model, state)
        vm.iconVisible.test().assertValue(defaultVisibility)

        model.onNext(defaultModel.copy(isVisible = changedVisibility))
        vm.iconVisible.test().assertValue(changedVisibility)
    }
}