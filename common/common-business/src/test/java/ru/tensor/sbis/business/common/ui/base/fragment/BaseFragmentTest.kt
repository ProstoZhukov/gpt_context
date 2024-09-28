package ru.tensor.sbis.business.common.ui.base.fragment

import android.os.Build
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.subjects.PublishSubject
import junit.framework.Assert.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowResources
import ru.tensor.sbis.business.common.databinding.FragmentBaseCrudListBinding
import ru.tensor.sbis.business.common.ui.base.PagingScrollHelper
import ru.tensor.sbis.business.common.ui.base.adapter.BaseListAdapter
import ru.tensor.sbis.business.common.ui.base.contract.MoneyTopNavigationState
import ru.tensor.sbis.business.common.ui.base.contract.ScreenContract
import ru.tensor.sbis.business.common.ui.utils.hideNavigationPanel
import ru.tensor.sbis.business.common.ui.viewmodel.BaseViewModel
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.mvvm.ViewModelFactory
import ru.tensor.sbis.business.theme.R as RBusinessTheme

@RunWith(AndroidJUnit4::class)
@Config(
    shadows = [ShadowResources::class],
    sdk = [Build.VERSION_CODES.P]
)
internal class BaseFragmentTest {

    private var testIsTablet = false
    private val testText = "Text"

    //region dependencies
    private val scrollEventSubject = PublishSubject.create<ScrollEvent>()
    private val mockScrollHelper = mock<ScrollHelper> {
        on { scrollEventObservable } doReturn scrollEventSubject
    }
    private val mockRxBus = mock<RxBus>()

    private inner class Factory(
        private val scrollHelper: ScrollHelper = mockScrollHelper,
        private val rxBus: RxBus = mockRxBus
    ) : FragmentFactory() {

        override fun instantiate(
            classLoader: ClassLoader,
            className: String
        ): Fragment = TestFragment().apply {
            factory = ViewModelFactory { TestScreenVm() }
            scrollHelper = this@Factory.scrollHelper
            rxBus = this@Factory.rxBus
        }
    }
    //endregion dependencies

    /**
     * При использовании [SbisTitleView] в разметке в таких тестах необходимо вызывать инициализацию фреско,
     * иначе происходит трудноотлаживаемый краш инфлейта
     */
    @Before
    fun before() {
        Fresco.initialize(
            ApplicationProvider.getApplicationContext(), null, DraweeConfig.newBuilder().build()
        )
    }

    @Test
    fun `Hide navigation panel on keyboard open measure`() {
        launchDefaultScenario().onFragment {
            it.onKeyboardOpenMeasure(0)

            verify(mockScrollHelper).hideNavigationPanel()
        }
    }

    @Test
    fun `Checks correct recycler view init state`() {
        launchDefaultScenario().onFragment {
            val recyclerView = it.provideBinding().listContainer.list as RecyclerView
            assertNotNull(recyclerView)
            with(recyclerView) {
                assertTrue(hasFixedSize())
                assertTrue(layoutManager is LinearLayoutManager)
                assertTrue(adapter is BaseListAdapter)
            }
        }
    }

    @Test
    fun `No scroll to top on tablet`() {
        testIsTablet = true
        launchDefaultScenario().onFragment {
            val recyclerView = it.provideBinding().listContainer.list as RecyclerView
            val layoutParams =
                it.provideBinding().collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams

            assertNotNull(recyclerView)
            assertNotNull(layoutParams)
            assertEquals(recyclerView.paddingBottom, 0)
            assertEquals(layoutParams.scrollFlags, 0)
        }
    }

    @After
    fun tearDown() {
        testIsTablet = false
    }

    internal class TestFragment : BaseFragment<TestScreenVm>(), PagingScrollHelper.ScrollInitiator {
        override val vmClass: Class<TestScreenVm> = TestScreenVm::class.java
        override fun provideInitiator(): () -> RecyclerView? = { recyclerView }

        fun provideBinding(): FragmentBaseCrudListBinding {
            return binding
        }
    }

    internal inner class TestScreenVm : BaseViewModel(), ScreenContract {
        override val state: StateFlow<MoneyTopNavigationState> = MutableStateFlow(MoneyTopNavigationState())
        override val showInitialProgress: ObservableBoolean = ObservableBoolean(false)
        override val isRefresh: ObservableBoolean = ObservableBoolean(false)

        override val list: ObservableField<List<BaseObservable>> = ObservableField(emptyList())

        override fun refreshForceUpdate() = Unit
    }

    private fun launchDefaultScenario(): FragmentScenario<TestFragment> =
        launchFragment(factory = Factory(), themeResId = RBusinessTheme.style.TestBusinessTheme)
}