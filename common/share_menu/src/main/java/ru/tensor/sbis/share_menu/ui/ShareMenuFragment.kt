package ru.tensor.sbis.share_menu.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.keyboard.KeyboardDetector
import ru.tensor.sbis.base_components.keyboard.keyboardDetector
import ru.tensor.sbis.base_components.keyboard.manageBy
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.containerAs
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.share_menu.ShareMenuPlugin
import ru.tensor.sbis.share_menu.ui.di.DaggerShareMenuComponent
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.share_menu.ui.view.ShareMenuController

/**
 * Компонент меню для фукнкции "поделиться".
 *
 * @author vv.chekurda
 */
internal class ShareMenuFragment : BaseFragment(),
    Content,
    KeyboardEventListener,
    KeyboardDetector.Delegate {

    /**
     * Реализация [ContentCreatorParcelable] для встраивания в dialog-контейнеры.
     *
     * @property shareData данные для "поделиться".
     * @property quickShareKey строковый идентификатор для "поделиться" недавним.
     */
    @Parcelize
    class Creator(
        private val shareData: ShareData,
        private val quickShareKey: String? = null
    ) : ContentCreatorParcelable {

        override fun createFragment(): Fragment =
            newInstance(shareData, quickShareKey)
    }

    companion object {

        /**
         * Создать экземпляр компонента меню для "поделиться".
         *
         * @param shareData данные для "поделиться".
         * @param quickShareKey строковый идентификатор для "поделиться" недавним.
         */
        fun newInstance(
            shareData: ShareData,
            quickShareKey: String? = null
        ): Fragment =
            ShareMenuFragment().withArgs {
                putParcelable(SHARE_MENU_SHARE_DATA_KEY, shareData)
                putString(SHARE_MENU_QUICK_SHARE_KEY, quickShareKey)
            }
    }

    private var controller: ShareMenuController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerShareMenuComponent.factory()
            .create(
                appContext = requireContext().applicationContext,
                contentContainerId = R.id.share_menu_content_container,
                shareData = requireArguments().getParcelable(SHARE_MENU_SHARE_DATA_KEY)!!,
                quickShareKey = requireArguments().getString(SHARE_MENU_QUICK_SHARE_KEY),
                dependency = ShareMenuPlugin.menuDependency
            ).also {
                controller = it.injector().inject(this)
            }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.share_menu_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initKeyboardDetector(view)
        disableKeyListener()
        if (savedInstanceState != null) {
            containerAs<Container.Showable>()?.showContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller = null
    }

    override fun onBackPressed(): Boolean =
        controller?.onBackPressed() ?: false

    override fun onCloseContent() {
        controller?.onCloseContent()
    }

    private fun disableKeyListener() {
        val isTablet = DeviceConfigurationUtils.isTablet(requireContext())
        if (isTablet) {
            (parentFragment as? DialogFragment)?.dialog?.setOnKeyListener(null)
        }
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
        getNestedKeyboardEventListener()
            ?.onKeyboardOpenMeasure(keyboardHeight)
            ?: false

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
        getNestedKeyboardEventListener()
            ?.onKeyboardCloseMeasure(keyboardHeight)
            ?: false

    private fun getNestedKeyboardEventListener(): KeyboardEventListener? =
        if (isAdded) {
            childFragmentManager.fragments
                .findLast { it is KeyboardEventListener }
                ?.let { it as KeyboardEventListener }
        } else {
            null
        }

    private fun initKeyboardDetector(view: View) {
        val calculationRect = Rect()
        var keyboardDetector: KeyboardDetector? = null
        var lastKeyboardHeight: Int
        var lastBottomContentOffset = 0
        val isTablet = DeviceConfigurationUtils.isTablet(view.context)
        val isLandscape = DeviceConfigurationUtils.isLandscape(view.context)
        val isPhoneLandscape = !isTablet && isLandscape

        fun getKeyboardOffsetForContent(keyboardHeight: Int): Int {
            lastKeyboardHeight = keyboardHeight
            val screenBottom = activity?.window?.decorView?.height ?: 0
            val contentBottom = view.getGlobalVisibleRect(calculationRect).let { calculationRect.bottom }
            var bottomContentOffset = screenBottom - contentBottom
            // Фикс от SDK, в landscape телефона getGlobalVisibleRect почему-то не зависит от поднятой клавиатуры.
            if (isPhoneLandscape && keyboardHeight > 0 && bottomContentOffset == 0) {
                bottomContentOffset = -1
            }
            return when {
                screenBottom == 0 -> 0
                // Кейс пересчета высоты, когда поднимается клавиатура одновременно с анимацией разворота шторки.
                !isTablet && keyboardHeight > 0 &&
                    (lastBottomContentOffset == 0 || lastBottomContentOffset != bottomContentOffset) -> {
                    lastBottomContentOffset = bottomContentOffset
                    view.postDelayed({ keyboardDetector?.onKeyboardOpenMeasure(lastKeyboardHeight) }, 16L)
                    keyboardHeight - (bottomContentOffset.takeIf { it > 0 } ?: 0)
                }
                isTablet -> {
                    val visibleContentBottom = view.getWindowVisibleDisplayFrame(calculationRect).let { calculationRect.bottom }
                    (contentBottom - visibleContentBottom).coerceAtLeast(0)
                }
                bottomContentOffset > 0 -> keyboardHeight - bottomContentOffset
                else -> keyboardHeight
            }
        }

        keyboardDetector = keyboardDetector(
            rootView = view,
            delegate = this,
            heightRecalculate = ::getKeyboardOffsetForContent
        ).also {
            it.manageBy(lifecycle)
        }
    }
}

private const val SHARE_MENU_SHARE_DATA_KEY = "SHARE_MENU_SHARE_DATA_KEY"
private const val SHARE_MENU_QUICK_SHARE_KEY = "SHARE_MENU_QUICK_SHARE_KEY"