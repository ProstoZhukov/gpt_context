package ru.tensor.sbis.onboarding.ui.host

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.tensor.sbis.design.utils.extentions.applyHeight
import ru.tensor.sbis.design.view_ext.UiUtils
import ru.tensor.sbis.design_dialogs.dialogs.container.base.BaseContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.mvvm.argument
import ru.tensor.sbis.onboarding.R
import ru.tensor.sbis.onboarding.databinding.OnboardingFragmentPagerBinding
import ru.tensor.sbis.onboarding.di.OnboardingSingletonComponentProvider
import ru.tensor.sbis.onboarding.di.ui.host.ARG_PAGE_COUNTER
import ru.tensor.sbis.onboarding.di.ui.host.DaggerHostFragmentComponent
import ru.tensor.sbis.onboarding.di.ui.host.HostFragmentModule
import ru.tensor.sbis.onboarding.domain.util.PermissionHelper
import ru.tensor.sbis.onboarding.ui.OnboardingActivity
import ru.tensor.sbis.onboarding.ui.base.OnboardingBackPress
import ru.tensor.sbis.onboarding.ui.base.OnboardingBaseFragment
import ru.tensor.sbis.onboarding.ui.base.OnboardingNextPage
import ru.tensor.sbis.onboarding.ui.host.adapter.FragmentPageAdapter
import ru.tensor.sbis.onboarding.ui.utils.RequestPermissionDelegate
import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider
import ru.tensor.sbis.onboarding.ui.utils.withArgs
import javax.inject.Inject
import javax.inject.Named

/**
 * Фрагмент отображения основного приветственного экрана
 *
 * @author as.chadov
 */
internal class OnboardingHostFragmentImpl :
    OnboardingBaseFragment<OnboardingHostVM, OnboardingFragmentPagerBinding>(),
    Content,
    HasAndroidInjector,
    RequestPermissionDelegate,
    OnboardingBackPress,
    OnboardingNextPage,
    ImageFrameListener,
    OnboardingHostFragment {

    val isDialogContent: Boolean by argument(ARG_AS_CONTENT)

    override val vmClass = OnboardingHostVM::class.java
    override val layoutId: Int = R.layout.onboarding_fragment_pager
    override val themeId: Int
        get() = themeProvider.getHostTheme(requireContext())

    @Inject
    @Named(ARG_PAGE_COUNTER)
    lateinit var pageCounterChannel: Subject<Int>

    @Inject
    lateinit var permissionHelper: PermissionHelper

    @Inject
    override lateinit var themeProvider: ThemeProvider

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>
    private val disposables = CompositeDisposable()
    private var selectedPage = PagerAdapter.POSITION_NONE

    /**
     * Вью-пейджер с контентом экрана приветствия
     */
    fun viewPager() = binding?.onboardingViewPager

    override fun androidInjector(): AndroidInjector<Any> = childFragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerHostFragmentComponent
            .builder()
            .onboardingSingletonComponent(OnboardingSingletonComponentProvider.get(requireContext()))
            .hostFragmentModule(HostFragmentModule(this))
            .build()
            .inject(this)
        savedInstanceState?.getInt(ARG_SELECTED_PAGE)?.let { selectedPage = it }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disableRotation()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startFlipTimerIfNeed()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopFlipTimerIfNeed()
    }

    override fun onDestroyView() {
        disposables.dispose()
        binding?.onboardingViewPager?.run {
            removeOnPageChangeListener(pagerChangeListener)
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        retainRotation()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.onboardingViewPager?.currentItem?.let { selectedPage ->
            outState.putInt(ARG_SELECTED_PAGE, selectedPage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) = permissionHelper.onRequestPermissionsResult(requestCode, grantResults)

    override fun requestPermissions(
        permissions: List<String>,
        requestCode: Int,
    ) {
        !isAdded && return
        requestPermissions(permissions.toTypedArray(), requestCode)
    }

    //region OnboardingBackPress
    override fun onBackPressed(): Boolean {
        if (viewModel.canSwipeBackPressed() && binding?.onboardingViewPager?.moveBackIfCan() == true) {
            return true
        }
        viewModel.onCloseOnboarding()
        return true
    }
    //endregion

    //region OnboardingNextPage
    override fun goNextPage(endless: Boolean) {
        binding?.onboardingViewPager?.let { pager ->
            if (endless.not()) {
                pager.currentItem++
            } else {
                val current = pager.currentItem + 1
                val count = pager.adapter?.count ?: 0
                if (current < count) {
                    pager.currentItem++
                } else {
                    pager.currentItem = 0
                }
            }
        }
    }

    override fun goPreviousPage() {
        binding?.onboardingViewPager?.let { pager ->
            pager.currentItem--
        }
    }
    //endregion OnboardingNextPage

    override fun onChangeFrame(height: Int) {
        binding?.onboardingPlaceholderImageContainer?.applyHeight(height)
    }

    override val getViewModel: OnboardingContract
        get() = viewModel

    private fun setupViewPager() {
        val adapter = FragmentPageAdapter(
            fragmentManager = childFragmentManager,
            creator = viewModel.creator,
            paramHolder = viewModel.holder
        )
        if (selectedPage != PagerAdapter.POSITION_NONE) {
            adapter.addOnRestoreItemAction(onRestoreItemAction)
        }
        val pager = binding?.onboardingViewPager ?: return
        pager.adapter = adapter
        pager.setOnSwipeListener(viewModel)
        pager.addOnPageChangeListener(pagerChangeListener)
        binding?.onboardingIndicator?.setupWithViewPager(pager)
        setupVisibilityIndicator()
        disposables.add(pageCounterChannel.subscribe {
            pager.adapter?.notifyDataSetChanged()
            setupVisibilityIndicator()
        })
    }

    private val onRestoreItemAction = Runnable {
        binding?.onboardingViewPager?.run {
            selectedPage == PagerAdapter.POSITION_NONE && return@Runnable
            adapter?.count ?: 0 <= selectedPage && return@Runnable
            adapter?.notifyDataSetChanged()
            currentItem = selectedPage
            selectedPage = PagerAdapter.POSITION_NONE
        }
    }

    private val pagerChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            val adapter = binding?.onboardingViewPager?.adapter
            if (adapter is FragmentPageAdapter) {
                viewModel.notifyOnPageChange(position)
            }
        }
    }

    private fun disableRotation() {
        if (themeProvider.isPreventRotation.not()) {
            return
        }
        if (activity !is OnboardingActivity) {
            activity?.let {
                viewModel.saveOriginOrientation(it.requestedOrientation)
                UiUtils.disableActivityRotation(it)
            }
        }
    }

    private fun retainRotation() {
        if (activity?.isActivityHostRunning == true) {
            viewModel.retainOriginOrientation()?.let {
                activity?.requestedOrientation = it
            }
        }
    }

    private fun setupVisibilityIndicator() {
        binding?.apply {
            onboardingIndicator.visibility =
                if (onboardingViewPager.adapter?.count ?: 0 > 1) View.VISIBLE else View.GONE
        }
    }

    private val Activity.isActivityHostRunning: Boolean
        get() = isDestroyed.not() && isFinishing.not() && isChangingConfigurations.not()

    //region Content
    /**
     * Реализация создателя экземпляра диалог фрагмента, используемого [BaseContainerDialogFragment]
     */
    class Creator : ContentCreator {
        override fun createFragment() = newInstance(isDialogContent = true)
    }

    override fun onCloseContent() = Unit
    //endregion Content

    companion object {
        fun newInstance(isDialogContent: Boolean = false) = OnboardingHostFragmentImpl().withArgs {
            putBoolean(ARG_AS_CONTENT, isDialogContent)
        }

        private const val ARG_AS_CONTENT = "ARG_AS_CONTENT"
        private const val ARG_SELECTED_PAGE = "ARG_SELECTED_PAGE"

        /**
         * Идентификатор фрагмента Приветсвенного экрана
         */
        internal const val ONBOARDING_TAG = "ONBOARDING_TAG"
    }
}