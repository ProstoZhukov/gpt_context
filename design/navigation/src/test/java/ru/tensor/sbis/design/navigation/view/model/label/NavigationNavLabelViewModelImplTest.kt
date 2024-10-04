package ru.tensor.sbis.design.navigation.view.model.label

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.subjects.BehaviorSubject
import junitparams.JUnitParamsRunner
import junitparams.custom.combined.CombinedParameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

private const val LABEL_RES = 1
private const val LABEL_ALIGNMENT_RES = 10

/**
 * Все тесты нужно проводить без подписок, чтобы гарантировать доставку события после подписки
 *
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class NavigationNavLabelViewModelImplTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val model = BehaviorSubject.create<NavigationItemLabel>()

    private lateinit var vm: NavigationNavLabelViewModel

    @Before
    fun setUp() {
        vm = NavigationNavLabelViewModelImpl(model)
    }

    @Suppress("JUnitMalformedDeclaration")
    @Test
    @CombinedParameters(
        "$LABEL_RES",
        "$LABEL_RES, $LABEL_ALIGNMENT_RES",
        // выравнивание не зависит от переопределённого заголовка
        "false, true"
    )
    fun `When vm receive model, then vm attributes should return values without changes`(
        label: Int,
        alignmentLabel: Int,
        alignment: Boolean
    ) {
        model.onNext(
            NavigationItemLabel(
                default = label,
                labelForRightAlignment = alignmentLabel,
                isAlignedRight = alignment
            )
        )
        vm.navViewLabel.test().assertValue(label)
        vm.labelForRightAlignment.test().assertValue(alignmentLabel)
        vm.isLabelAlignedRight.test().assertValue(alignment)
    }

    @Test
    fun `When label changed, then vm label should be changed`() {
        val changedLabel = LABEL_RES.inc()

        model.onNext(NavigationItemLabel(LABEL_RES))
        assertEquals(LABEL_RES, vm.navViewLabel.blockingFirst())

        model.onNext(NavigationItemLabel(changedLabel))
        assertEquals(changedLabel, vm.navViewLabel.blockingFirst())
    }

    @Test
    fun `When label for alignment changed, then vm label for alignment should be changed`() {
        val changedLabel = LABEL_ALIGNMENT_RES.inc()

        model.onNext(NavigationItemLabel(0, labelForRightAlignment = LABEL_ALIGNMENT_RES))
        assertEquals(LABEL_ALIGNMENT_RES, vm.labelForRightAlignment.blockingFirst())

        model.onNext(NavigationItemLabel(0, labelForRightAlignment = changedLabel))
        assertEquals(changedLabel, vm.labelForRightAlignment.blockingFirst())
    }

    @Test
    fun `When alignment flag changed, then vm alignment flag should be changed`() {
        val navigationItemLabel = NavigationItemLabel(0, isAlignedRight = false)
        val changedAlignedFlag = navigationItemLabel.isAlignedRight.not()

        model.onNext(navigationItemLabel)
        vm.isLabelAlignedRight.test().assertValue(navigationItemLabel.isAlignedRight)

        model.onNext(NavigationItemLabel(0, isAlignedRight = changedAlignedFlag))
        vm.isLabelAlignedRight.test().assertValue(changedAlignedFlag)

    }
}