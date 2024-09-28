package ru.tensor.sbis.master_detail

import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.annotation.VisibleForTesting
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.design.stubview.ResourceAttributeStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.utils.checkSafe
import ru.tensor.sbis.list.BuildConfig
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.master_detail.utils.CallbacksForSelectionHighlighting
import timber.log.Timber
import ru.tensor.sbis.design.stubview.R as RStubView
import ru.tensor.sbis.list.R as RList
import ru.tensor.sbis.master_detail.R as RMasterDetail

/**
 *
 * Реализация компоновки экрана вида Master-Detail, Master - фрагмент со списком сущностей, по клику на элемент
 * которого отображается Detail фрагмент с детальной информацией о элементе списка.
 * На планшете Master фрагмент отображается в левой части экрана, а Detail в правой.
 * На телефоне Master фрагмент отображается на весь экран, Detail фрагмент отображается поверх него.
 * Компонент обеспечивает всю необходимую работу со стеком при добавлении Detail фрагмента, определяет сам когда нужно
 * показывать планшетный вариант, а когда вариант для телефона, то в зависимости от размера экрана, а так же события
 * нажатия кнопки возврата(back press).
 *
 * http://axure.tensor.ru/MobileStandart8/#p=%D0%BA%D0%BE%D0%BC%D0%BF%D0%BE%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0_%D0%BF%D0%BE%D0%B4_%D0%BF%D0%BB%D0%B0%D0%BD%D1%88%D0%B5%D1%82&g=1
 *
 * Пример использования:
 * class HostFragment : MasterDetailFragment() { *
 * override fun createMasterFragment() = ContactListFragment()
 * }
 *
 * val hostFragment: HostFragment = ...
 * hostFragment.showDetailFragment(SomeFragment())
 * ...
 * hostFragment.removeDetailFragment()
 *
 * Методы для создания и инициализации View уже определены.
 * Если Master фрагмент содержит [ru.tensor.sbis.list.view.SbisList], то в нем будет включен режим выделения строк
 * при показе компоновки планшета и выделение будет сбрасываться при удалении Detail фрагмента.
 *
 * @author du.bykov
 */
abstract class MasterDetailFragment @VisibleForTesting internal constructor(
    private val callbacksForSelectionHighlighting: CallbacksForSelectionHighlighting
) :
    Fragment(), DetailFragmentManager {

    constructor() : this(CallbacksForSelectionHighlighting())

    private val isTablet get() = view?.findViewById<FrameLayout>(RMasterDetail.id.detail_container) != null
    private val detailContainerStubView get() = view?.findViewById<StubView>(RMasterDetail.id.detail_container_stub)
    private val detailToolbarView get() = view?.findViewById<View>(RMasterDetail.id.detail_container_toolbar)
    private val detailToolbarDivider get() = view?.findViewById<View>(RMasterDetail.id.detail_container_toolbar_divider)
    private val viewModel get() = ViewModelProvider(this)[MasterDetailViewModel::class.java]

    /**
     * Нужно ли отображать разделитель у фэйкового тулбара в заглушке detail
     */
    protected open var isVisibleToolbarDivider: Boolean = false

    /**
     * Текст заглушки. Необходимо переопределить в зависимости от экрана реестра, на котором используется фрагмент.
     */
    open var stubText: PlatformSbisString =
        PlatformSbisString.Res(RStubView.string.design_stub_view_split_view_container_details)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) return

        showMasterFragment()
    }

    /**
     * Создать View отображающую компоновку фрагментов в нужном виде, в зависимости от размера и ориентации девайса.
     *
     * @param inflater @SelfDocumented
     * @param container @SelfDocumented
     * @param savedInstanceState @SelfDocumented
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = createCustomLayoutInflater(inflater).inflate(
            RMasterDetail.layout.master_detail_fragment,
            container,
            false
        )!!

        viewModel.apply {
            detailContainerStubViewVisibility.observe(viewLifecycleOwner) {
                detailContainerStubView?.visibility = it
            }
        }

        return inflate
    }

    /**
     * Добавить обработку событий Back Pressed и подсветки строк в SbisList в Master фрагменте.
     *
     * @param view @SelfDocumented
     * @param savedInstanceState @SelfDocumented
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isTablet) return

        onBackPressedCallback.isEnabled = savedInstanceState != null && childFragmentManager.backStackEntryCount > 0

        childFragmentManager.registerFragmentLifecycleCallbacks(
            callbacksForSelectionHighlighting,
            false
        )
        view.setBackgroundColor(ColorProvider(requireContext()).contentBackground)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        detailToolbarDivider?.isVisible = isVisibleToolbarDivider
        detailContainerStubView?.setContent(
            ResourceAttributeStubContent(ID_NULL, null, stubText.getString(requireContext()), emptyMap())
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.commandRunner.resume(this, childFragmentManager)
    }

    override fun onPause() {
        super.onPause()
        viewModel.commandRunner.pause()
    }

    /**
     * На планшете Master фрагмент отобразится в левой части экрана, а на телефоне - на весь экран, Detail фрагменты
     * будут отображены поверх него. Методы вызовется по готовности MasterDetailFragment для отображения начального
     * состояния.
     *
     * @return фрагмент должен либо содержать SbisList, либо реализовывать интерфейс [SelectionHelper], чтобы корректно отработать
     * событие удаления Detail фрагмента.
     */
    abstract fun createMasterFragment(): Fragment

    override fun showDetailFragment(
        fragment: Fragment,
        swipeable: Boolean,
        tag: String?,
        popPreviousFromBackStack: Boolean
    ) {
        if (isTablet) {
            showInDetailContainer(fragment, tag, popPreviousFromBackStack)
        } else {
            showAbove(fragment, swipeable, tag)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    /**
     * Указание делегата для отобрадения детального фрагмент поверх вего контента приложения. По умолчанию, пытается
     * использовать Activity.
     */
    protected open fun getOverlayFragmentHolder(): OverlayFragmentHolder? {
        checkSafe(activity is OverlayFragmentHolder) { "Активити $activity не реализует интерфейс OverlayFragmentHolder" }

        return activity as OverlayFragmentHolder
    }

    override fun showDetailFragment(
        createFragmentForPhone: () -> Fragment,
        createFragmentForTablet: () -> Fragment
    ) {
        showDetailFragment(if (isTablet) createFragmentForTablet() else createFragmentForPhone())
    }

    /**
     * Удаление Detail фрагмент, при этом, в планшетной компоновке, у Master фрагмента будет вызван метод [SelectionHelper.cleanSelection].
     * А если Master использует SbisList, то дополнительно вызовется метод [SbisList.cleanSelection].
     */
    override fun removeDetailFragment() {
        if (childFragmentManager.backStackEntryCount > 0) {
            viewModel.commandRunner.runCommandSticky { _, _ ->
                childFragmentManager.popBackStack()
            }
        }

        if (isTablet && childFragmentManager.backStackEntryCount == 1) {
            viewModel.deletedDetailFragment()
            callCleanSelectionOnMaster()
            onBackPressedCallback.isEnabled = false
        }
    }

    private fun showAbove(fragment: Fragment, swipeable: Boolean, tag: String? = null) {
        getOverlayFragmentHolder()?.let { holder ->
            tag?.let { holder.setFragmentWithTag(fragment, swipeable, it) } ?: holder.setFragment(fragment, swipeable)
        }
    }

    private fun showInDetailContainer(
        fragment: Fragment,
        tag: String?,
        popPreviousFromBackStack: Boolean
    ) {
        if (popPreviousFromBackStack) {
            viewModel.commandRunner.runCommandSticky { _, _ ->
                childFragmentManager.popBackStack()
            }
        }
        childFragmentManager.beginTransaction().apply {
            addToBackStack(tag ?: ROOT_STACK_KEY)
            addTransactionWithFadeAnimationAndReplacement(fragment, tag)
            commitAllowingStateLoss()
        }
        childFragmentManager.addFragmentOnAttachListener { _, _ ->
            viewModel.addedDetailFragment()
            onBackPressedCallback.isEnabled = true
        }
    }

    @CallSuper
    protected open fun createCustomLayoutInflater(inflater: LayoutInflater): LayoutInflater =
        inflater.cloneInContext(ContextThemeWrapper(activity, getThemeRes()))

    @StyleRes
    private fun getThemeRes(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(RStubView.attr.stubViewTheme, typedValue, true)
        if (typedValue.data == 0) return RStubView.style.StubViewDefaultTheme

        return typedValue.data
    }

    /**
     * Если Master фрагмент использует SbisList, то будет вызван [SbisList.cleanSelection], иначе, если Master фрагмент
     * реализует интерфейс [SelectionHelper], будет вызван метод [SelectionHelper.cleanSelection].
     * Если не произойдет ни того ни другого, будет выброшено исключения [IllegalStateException] в DEBUG сборке, а для
     * RELEASE сборки, это исключение будет залогировано.
     */
    private fun callCleanSelectionOnMaster() {
        val fragment = childFragmentManager.findFragmentById(masterContainer)

        if (!tryCleanSelectionOnSbisList(fragment))
            if (fragment is SelectionHelper) {
                fragment.cleanSelection()
                return
            }
        val exception =
            IllegalStateException("Master-fragment is supposed to implement Master interface, but it doesn't")

        if (BuildConfig.DEBUG) Timber.e(exception)
        else Timber.d(exception)
    }

    private fun tryCleanSelectionOnSbisList(fragment: Fragment?): Boolean {
        val sbisList =
            fragment?.requireView()?.findViewById<SbisList>(RList.id.list_sbisList) ?: return false
        sbisList.cleanSelection()
        return true
    }

    private fun showMasterFragment() {
        childFragmentManager
            .beginTransaction()
            .add(
                masterContainer,
                createMasterFragment()
            )
            .commitAllowingStateLoss()
    }

    private fun FragmentTransaction.addTransactionWithFadeAnimationAndReplacement(
        fragment: Fragment,
        tag: String?
    ) {
        replace(
            detailContainer,
            fragment,
            tag
        )
    }

    protected open val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val detailFragment = childFragmentManager.findFragmentById(detailContainer)
            if (detailFragment is FragmentBackPress) {
                val result = detailFragment.onBackPressed()
                if (!result) removeDetailFragment()
            } else {
                removeDetailFragment()
            }
        }
    }

    internal companion object {
        const val ROOT_STACK_KEY = "ROOT"
        val masterContainer = RMasterDetail.id.master_container
        val detailContainer = RMasterDetail.id.detail_container
    }
}
