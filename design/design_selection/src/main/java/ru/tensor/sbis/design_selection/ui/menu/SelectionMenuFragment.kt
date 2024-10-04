package ru.tensor.sbis.design_selection.ui.menu

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communication_decl.selection.SelectionMenuDelegate
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_selection.ui.main.SelectionFragment
import ru.tensor.sbis.design_selection.ui.main.utils.autoHideKeyboard
import ru.tensor.sbis.design_selection.ui.main.utils.itemsAnimationDurationMs
import ru.tensor.sbis.design_selection.ui.main.utils.showLoaders
import ru.tensor.sbis.design_selection.ui.main.utils.showStubs
import ru.tensor.sbis.design_selection.ui.main.utils.useRouterReplaceStrategy
import ru.tensor.sbis.design_selection.ui.menu.vm.SelectionMenuViewModel
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegate
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl

/**
 * Фрагмент меню выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionMenuFragment :
    BaseFragment(),
    ContainerMovableDelegate by ContainerMovableDelegateImpl(true),
    SelectionMenu {

    /**
     * Билдер фрагмента меню выбора.
     */
    class Builder : ContainerMovableDelegateImpl.AbstractBuilder<SelectionMenuFragment>() {

        /**
         * Установить [ContentCreatorParcelable] с фрагментом выбора.
         */
        fun setSelectionContentCreator(creator: ContentCreatorParcelable): Builder = apply {
            bundle.putParcelable(CONTENT_CREATOR_KEY, creator)
        }

        /**
         * Установить режим для автоматического скрытия меню выбора, если список пустой.
         */
        fun setAutoHideEmptyMenu(hide: Boolean): Builder = apply {
            bundle.putBoolean(AUTO_HIDE_KEY, hide)
        }

        /**
         * Установить режим игнорирования инсетов.
         * Использовать, если ваши контйнеры уже находятся над клавиатурой.
         */
        fun setIgnoreWindowInsets(ignore: Boolean): Builder = apply {
            bundle.putBoolean(IGNORE_KEYBOARD_INSETS_KEY, ignore)
        }

        /**
         * Установить режим показа заглушек.
         * Использовать, если ваше меню не подразумевает возможность отображения заглушек.
         */
        fun setShowStubs(show: Boolean): Builder = apply {
            bundle.putBoolean(SHOW_STUBS_KEY, show)
        }

        /**
         * Установить режим показа прогрессов загрузки.
         * Использовать, если ваше меню может оборачиваться по контенту, и прогрессы могут вызвать лишние скачки.
         */
        fun setShowLoaders(show: Boolean): Builder = apply {
            bundle.putBoolean(SHOW_LOADERS_KEY, show)
        }

        override fun build(): SelectionMenuFragment = newInstance(bundle)
    }

    companion object {
        private const val CONTENT_CREATOR_KEY = "CONTENT_CREATOR_KEY"
        private const val AUTO_HIDE_KEY = "AUTO_HIDE_KEY"
        private const val IGNORE_KEYBOARD_INSETS_KEY = "IGNORE_KEYBOARD_INSETS_KEY"
        private const val SHOW_STUBS_KEY = "SHOW_STUBS_KEY"
        private const val SHOW_LOADERS_KEY = "SHOW_LOADERS_KEY"

        private fun newInstance(args: Bundle) =
            SelectionMenuFragment().apply { arguments = args }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[SelectionMenuViewModel::class.java]
    }

    private val movableContainerId = R.id.modalwindows_movable_panel_container_id

    private val selectionFragment: SelectionFragment?
        get() = childFragmentManager.findFragmentById(movableContainerId)
            as? SelectionFragment

    override fun setupMenu(fragmentManager: FragmentManager, containerId: Int) {
        fragmentManager.beginTransaction()
            .add(containerId, this)
            .commitNowAllowingStateLoss()
    }

    override fun getSelectionMenuDelegate(): SelectionMenuDelegate = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        createView(inflater, container, requireArguments())!!.also {
            placeSelectionFragment()
            viewCreated(it, activity, childFragmentManager)

            val movablePanel = (it as MovablePanel)
            movablePanel.setShadowEnabled(false)
            movablePanel.animateParentHeightChanges = false
            val ignoreInsets = requireArguments().getBoolean(IGNORE_KEYBOARD_INSETS_KEY)
            if (ignoreInsets) {
                movablePanel.contentContainer?.setOnApplyWindowInsetsListener(null)
            }
            stateCallback = viewModel::onMenuStateChanged
            slideCallback = { slidePosition ->
                view?.isInvisible = slidePosition == 0f
            }
            setCurrentScrollViewProvider { findScrollView(it) }

            initViewModel()
        }

    private fun placeSelectionFragment() {
        if (selectionFragment != null) return

        val contentCreator: ContentCreatorParcelable =
            requireNotNull(requireArguments().getParcelableUniversally(CONTENT_CREATOR_KEY))
        val fragment = contentCreator.createFragment()
        fragment.requireArguments().apply {
            itemsAnimationDurationMs = ITEM_ANIMATION_DURATION_MS
            useRouterReplaceStrategy = true
            autoHideKeyboard = false
            showStubs = requireArguments().getBoolean(SHOW_STUBS_KEY)
            showLoaders = requireArguments().getBoolean(SHOW_LOADERS_KEY)
        }

        childFragmentManager.beginTransaction()
            .add(movableContainerId, fragment)
            .commitNowAllowingStateLoss()
    }

    private fun initViewModel() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.menuPeekHeightType.collect { type ->
                // Для плавного первого появления по окончанию всех измерений
                if (type == ContainerMovableDelegateImpl.PeekHeightType.INIT &&
                    view != null &&
                    findScrollView(requireView())?.measuredHeight == 0
                ) {
                    Looper.getMainLooper().queue.addIdleHandler {
                        if (view == null) return@addIdleHandler false
                        changePeekHeight(type)
                        false
                    }
                } else {
                    changePeekHeight(type)
                }
            }
        }

        val selectionDelegate = requireNotNull(selectionFragment).getSelectionDelegate()
        viewModel.setSelectionDelegate(selectionDelegate)
    }

    private fun changePeekHeight(type: ContainerMovableDelegateImpl.PeekHeightType) {
        view?.isInvisible = type == ContainerMovableDelegateImpl.PeekHeightType.HIDDEN
        setPeekHeight(type)
    }

    private fun findScrollView(view: View): View? {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view
        }
        var scrollingChild: View? = null
        if (view is ViewGroup) {
            for (i in view.childCount - 1 downTo 0) {
                val child = view.getChildAt(i)
                scrollingChild = findScrollView(child)
                if (scrollingChild != null) {
                    break
                }
            }
        }
        return scrollingChild
    }

    override fun onDestroyView() {
        destroyView()
        setCurrentScrollViewProvider { null }
        super.onDestroyView()
    }

    override fun onDestroy() {
        destroy()
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean = false
}

private const val ITEM_ANIMATION_DURATION_MS = 150L