package ru.tensor.sbis.business.common.ui.base.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.business.common.ui.fragment.popLastBackStackState
import ru.tensor.sbis.business.common.ui.viewmodel.BaseViewModel
import ru.tensor.sbis.design.swipeback.SwipeBackHelper
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.DragDirectMode.DIRECTION_FROM_EDGE
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.mvvm.*
import javax.inject.Inject

/**
 * Базовый класс MVVM [Fragment].
 * Содержит обработчики:
 * - нажатия кнопки "Назад"
 * - свайпа назад
 *
 * @author as.chadov
 *
 * @param VIEW_MODEL тип вьюмодели
 * @param BINDING тип [ViewDataBinding], соответствующий layout-ресурсу [View] фрагмента
 *
 * @property binding сгенерированный датабиндинг класс для экрана [ViewDataBinding]
 * @property factory фабрика отвечающая за создание джереник [VIEW_MODEL] экземпляра вью-модели
 */
abstract class VMFragment<VIEW_MODEL : BaseViewModel, BINDING : ViewDataBinding> : Fragment(),
    FragmentBackPress,
    SwipeBackLayout.SwipeBackListener {

    private val viewModelDelegate = lazy {
        withFactory(factory, vmClass).apply { initialize() }
    }

    @Suppress("LeakingThis")
    protected val viewModel: VIEW_MODEL by viewModelDelegate

    /** Ресур идентификатора макета экрана. */
    @get:LayoutRes
    protected abstract val layoutId: Int

    /** Ресур идентификатора темы экрана. */
    @get:StyleRes
    protected open val themeId: Int = ID_NULL

    /** Тип вью-модели экрана. */
    protected abstract val vmClass: Class<VIEW_MODEL>
    protected var binding by autoCleared<BINDING>()

    val isViewModelDelegateInitialized
        get() = viewModelDelegate.isInitialized()

    @Inject
    lateinit var factory: ViewModelFactory<VIEW_MODEL>

    private val viewDisposableHandle = createViewDisposableHandler()
    private var hasSavedInstanceState = false

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    var swipeBackHelper: SwipeBackHelper? = null
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        set

    //region Fragment
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.initialize(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = if (themeId != ID_NULL) {
            val compoundTheme: Resources.Theme = resources.newTheme().also {
                it.setTo(requireContext().theme)
                it.applyStyle(themeId, true)
            }
            inflater.cloneInContext(ContextThemeWrapper(requireContext(), compoundTheme))
        } else {
            inflater
        }.let {
            DataBindingUtil.inflate(it, layoutId, container, false)
        }
        if (!binding.setVariable(BR.viewModel, viewModel)) {
            throw RuntimeException("Layout XML resource should contain data variable with name=\"viewModel\"")
        }
        return if (swipeBackEnabled()) {
            swipeBackHelper = SwipeBackHelper(requireActivity(), this@VMFragment)
            val swipeRootView = swipeBackHelper!!.container
            swipeBackHelper!!.swipeBackLayout.addView(binding.root)
            swipeBackHelper!!.swipeBackLayout.setDragDirectMode(DIRECTION_FROM_EDGE)
            swipeRootView
        } else binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        swipeBackHelper?.setOnSwipeBackListener(null)
        swipeBackHelper = null
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        fixIssueWithRepeatingTransitionAnimationAfterRotation()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }
    //endregion

    //region mvvm
    /**
     * Добавить подписку привязанную к жизненному циклу фрагмента.
     * Для [retainInstance] фрагментов:
     * - если вызывать метод в [Fragment.onViewCreated] необходимо определить очистку
     * ресурсов [ViewDisposableHandler] в [Fragment.onDestroyView]
     * - если вызывать метод в [Fragment.onCreate] то доп. обработок не требуется
     */
    protected fun addViewDisposable(factory: () -> Disposable) =
        viewDisposableHandle.addFactory(factory)
    //endregion

    /**
     * Исправление бага, связанного с setRetainInstance, когда анимация показа фрагмента воспроизводиться повторно, после поворота экрана
     */
    private fun fixIssueWithRepeatingTransitionAnimationAfterRotation() {
        try {
            val field = Fragment::class.java.getDeclaredField("mAnimationInfo")
            field.isAccessible = true
            field.set(this, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //region Навигация
    override fun onViewPositionChanged(
        fractionAnchor: Float,
        fractionScreen: Float,
    ) {
        swipeBackHelper?.ivShadow?.alpha = 1 - fractionScreen
    }

    override fun onViewGoneBySwipe() {
        parentFragmentManager.popLastBackStackState()
    }

    /**
     * Делегирует обработку события нажатия кнопки "Назад" дочерним фрагментам реализующим [FragmentBackPress].
     * Если никто из них его не обработал, то обрабатывает самостоятельно через бэкстэк
     *
     * @return было ли обработано нажатие кнопки "Назад"
     */
    //TODO https://online.sbis.ru/opendoc.html?guid=4a871e34-3c51-4f45-82ed-119eb3fa12f3
    override fun onBackPressed(): Boolean {
        val isSectionRoot = this is SectionFragmentsContainer
        /* делегируем обработку дочерним фрагментам начиная с последних добавленных, если
        * те реализуют [FragmentBackPress] и видны пользователю */
        for (childFragment in childFragmentManager.fragments.reversed()) {
            if (childFragment is FragmentBackPress && childFragment.isVisible) {
                // отдаем обработку на откуп найденному возможному потребителю
                if ((childFragment as FragmentBackPress).onBackPressed()) {
                    // прекращаем обработку если потребитель обработал
                    return true
                } else if (isSectionRoot &&
                    childFragmentManager.popLastBackStackState(MIN_BACK_STACK_ROOT_SIZE)
                ) {
                    // выполнен переход по стеку в фрагменте раздела
                    return true
                }
            }
        }
        // дочерних потребителей нет, пробуем обработать через навигацию по бэкстэку
        // прекращаем обработку если фрагмент был удален из бэкстэка
        if (isSectionRoot && childFragmentManager.popLastBackStackState(MIN_BACK_STACK_ROOT_SIZE)) {
            return true
        }
        return false
    }

    protected open fun swipeBackEnabled(): Boolean {
        val tag = tag ?: javaClass.canonicalName
        return parentFragment?.childFragmentManager?.run {
            backStackEntryCount > 1 && tag != getBackStackEntryAt(0).name
        } == true
    }

    companion object {
        /** размер backStack'а [SectionFragmentsContainer] для предотвращения вычистки хоста */
        private const val MIN_BACK_STACK_ROOT_SIZE: Int = 2
    }
    //endregion Навигация
}
