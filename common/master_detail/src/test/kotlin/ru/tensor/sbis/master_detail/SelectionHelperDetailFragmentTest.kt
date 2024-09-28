package ru.tensor.sbis.master_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.master_detail.utils.CallbacksForSelectionHighlighting
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.master_detail.R as RMasterDetail

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
@Ignore
internal class SelectionHelperDetailFragmentTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()
    private val mockOverlayFragmentHolder = mock<OverlayFragmentHolder>()

    private val mockCallback = mock<CallbacksForSelectionHighlighting>()
    private val scenario = launchFragmentInContainer(themeResId = RDesign.style.AppTheme) {
        TestMasterDetailFragment(mockOverlayFragmentHolder, mockCallback)
    }

    @Test
    fun default() {
        object : MasterDetailFragment() {
            override fun createMasterFragment() = Fragment()
        }
    }

    @Test
    @Config(qualifiers = "sw599dp")
    fun `Given phone started, show only master fragment without selection mode`() {
        val (spyTestMasterDetailFragment, testMasterFragment) = createSpyTestMasterDetailFragment()

        launchFragmentInContainerWithTheme {
            spyTestMasterDetailFragment
        }
            .onFragment {
                val fragmentInMasterContainer =
                    it.childFragmentManager.findFragmentById(RMasterDetail.id.master_container)
                assertSame(testMasterFragment, fragmentInMasterContainer)
                verify(testMasterFragment, never()).shouldHighlightSelectedItems()
            }
    }

    @Test
    @Config(qualifiers = "sw599dp")
    fun `Given phone started, when add detail fragment, then show it in master container`() {
        scenario.onFragment {
            val fragment = TestDetailFragment0()
            it.showDetailFragment(fragment)

            verify(mockOverlayFragmentHolder).setFragment(fragment)
        }
    }

    @Test
    @Config(qualifiers = "sw599dp")
    fun `recreate on phone`() {
        scenario.recreate()
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `recreate on tablet`() {
        scenario.recreate()
    }
//    @Test
//    @Config(qualifiers = "sw599dp")
//    fun `Given phone started, when add detail fragment and remove detail fragment, then show master fragment`() {
//        scenario.onFragment {
//            it.showDetailFragment(TestDetailFragment0())
//            it.removeDetailFragment()
//
//            assertTrue(it.childFragmentManager.findFragmentById(R.id.master_container) is TestMasterFragment)
//        }
//    }

    @Test
    @Config(qualifiers = "sw599dp")
    fun `Given phone started and detail fragment added, when back press, then show master fragment`() {
        scenario.onFragment {
            it.showDetailFragment(TestDetailFragment0())

            it.activity!!.onBackPressed()

            assertTrue(it.childFragmentManager.findFragmentById(RMasterDetail.id.master_container) is TestSelectionHelperFragment)
        }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Given table started, show master fragment and empty container for detail and master fragment has selection mode`() {
        val (spyTestMasterDetailFragment, _) = createSpyTestMasterDetailFragment()

        launchFragmentInContainerWithTheme {
            spyTestMasterDetailFragment
        }
            .onFragment { fragment ->
                verifyTabletInitialState(fragment)
                verify(mockCallback).onFragmentViewCreated(
                    any(),
                    isA<TestSelectionHelperFragment>(),
                    any(),
                    eq(null)
                )
            }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Given table started, when add detail fragment, then show it in detail container`() {
        scenario.onFragment {
            it.showDetailFragment(TestDetailFragment0())
            it.childFragmentManager.executePendingTransactions()
            assertTrue(it.childFragmentManager.findFragmentById(RMasterDetail.id.detail_container) is TestDetailFragment0)
        }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Given table started, when add detail fragment and remove detail fragment, then show only master fragment`() {
        scenario.onFragment {
            it.showDetailFragment(TestDetailFragment0())
            it.removeDetailFragment()

            verifyTabletInitialState(it)
        }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Given table started, when remove single detail fragment, then clean selection in master fragment`() {
        val (spyTestMasterDetailFragment, spyTestMasterFragment) = createSpyTestMasterDetailFragment()

        launchFragmentInContainerWithTheme {
            spyTestMasterDetailFragment
        }
            .onFragment {
                it.showDetailFragment(TestDetailFragment0())
                //act
                it.removeDetailFragment()
                //verify
                verify(spyTestMasterFragment).cleanSelection()
            }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Given table started, when remove not single detail fragment, then don't clean selection in master fragment`() {
        val (spyTestMasterDetailFragment, spyTestMasterFragment) = createSpyTestMasterDetailFragment()

        launchFragmentInContainerWithTheme {
            spyTestMasterDetailFragment
        }
            .onFragment {
                it.showDetailFragment(TestDetailFragment0())
                it.showDetailFragment(TestDetailFragment1())
                it.childFragmentManager.executePendingTransactions()
                //act
                it.removeDetailFragment()
                //verify
                verify(spyTestMasterFragment).cleanSelection()
            }
    }

    private fun launchFragmentInContainerWithTheme(instantiate: () -> TestMasterDetailFragment) =
        launchFragmentInContainer(themeResId = RDesign.style.AppTheme, instantiate = instantiate)

    //region back press
    @Test
    @Config(qualifiers = "sw599dp")
    fun `Given phone started, when back press, run default activity action`() {
        scenario.onFragment {
            it.activity!!.onBackPressed()

            verifyDefaultBackPressAction(it)
        }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Given table started and detail fragment is shown, when back press, run default activity action`() {
        scenario.onFragment {
            it.showDetailFragment(TestDetailFragment0())

            it.activity!!.onBackPressed()

            verifyDefaultBackPressAction(it)
        }
    }

    //endregion
    //region private
    private fun verifyTabletInitialState(it: TestMasterDetailFragment) {
        assertTrue(it.childFragmentManager.findFragmentById(RMasterDetail.id.master_container) is TestSelectionHelperFragment)
        assertNotEquals(null, it.view!!.findViewById(RMasterDetail.id.detail_container))
        assertEquals(null, it.childFragmentManager.findFragmentById(RMasterDetail.id.detail_container))
    }

    private fun createSpyTestMasterDetailFragment(): Pair<TestMasterDetailFragment, TestSelectionHelperFragment> {
        val spyTestMasterDetailFragment = spy(TestMasterDetailFragment(mockOverlayFragmentHolder, mock()))
        val spyTestMasterFragment = spy<TestSelectionHelperFragment>()
        whenever(spyTestMasterDetailFragment.createMasterFragment()).doReturn(spyTestMasterFragment)
        return Pair(spyTestMasterDetailFragment, spyTestMasterFragment)
    }

    private fun verifyDefaultBackPressAction(it: TestMasterDetailFragment) {
        /**
         * Никаких действий для Activity нет, а этим тестом проверим, что событие передалось в Activity
         */
        assertTrue(it.activity!!.isFinishing)
    }
    //endregion

}

class TestSelectionHelperFragment : Fragment(), SelectionHelper {

    override fun cleanSelection() {
    }

    override fun shouldHighlightSelectedItems() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return View(context)
    }
}

class TestMasterDetailFragment internal constructor(
    private val _overlayFragmentHolder: OverlayFragmentHolder,
    mockCallback: CallbacksForSelectionHighlighting
) :
    MasterDetailFragment(mockCallback) {

    override fun createMasterFragment() = TestSelectionHelperFragment()

    override fun getOverlayFragmentHolder(): OverlayFragmentHolder {
        return _overlayFragmentHolder
    }
}

class TestDetailFragment0 : Fragment()

class TestDetailFragment1 : Fragment()