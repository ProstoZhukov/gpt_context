package ru.tensor.sbis.design.navigation.view.model.label

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.subjects.BehaviorSubject
import junitparams.JUnitParamsRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString

private const val LABEL_HORIZONTAL_RES = 1
private const val LABEL_VERTICAL_RES = 10

/**
 * Все тесты нужно проводить без подписок, чтобы гарантировать доставку события после подписки
 *
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class NavigationLabelViewModelImplTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val model = BehaviorSubject.create<NavigationItemLabel>()

    private lateinit var vm: NavigationLabelViewModel

    @Before
    fun setUp() {
        vm = NavigationLabelViewModelImpl(model)
    }

    @Test
    fun `When horizontal label changed, then vm horizontal label should be changed`() {
        val changedLabel = LABEL_HORIZONTAL_RES.inc()

        model.onNext(NavigationItemLabel(0, LABEL_HORIZONTAL_RES))

        model.test().assertValue(NavigationItemLabel(0, LABEL_HORIZONTAL_RES))
        vm.navigationLabel.test().assertValue {
            LABEL_HORIZONTAL_RES == it.short.resId
        }

        model.onNext(NavigationItemLabel(0, changedLabel))
        vm.navigationLabel.test().assertValue {
            changedLabel == it.short.resId
        }
    }

    @Test
    fun `When vertical label changed, then vm vertical label should be changed`() {
        val changedLabel = LABEL_VERTICAL_RES.inc()

        model.onNext(NavigationItemLabel(LABEL_VERTICAL_RES))
        vm.navigationLabel.test().assertValue {
            LABEL_VERTICAL_RES == it.default.resId
        }

        model.onNext(NavigationItemLabel(changedLabel))
        vm.navigationLabel.test().assertValue {
            changedLabel == it.default.resId
        }
    }

    private val SbisString.resId
        get() = (this as PlatformSbisString.Res).stringRes
}