package ru.tensor.sbis.marks.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.VERTICAL
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.getParcelableArrayListUniversally
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.utils.createCancelHeaderButton
import ru.tensor.sbis.marks.utils.createHeaderView
import ru.tensor.sbis.marks.panel.SbisMarksMovablePanelLauncher.Companion.COMPONENT_TYPE_STATE
import ru.tensor.sbis.marks.panel.SbisMarksMovablePanelLauncher.Companion.FRAGMENT_RESULT_KEY
import ru.tensor.sbis.marks.panel.SbisMarksMovablePanelLauncher.Companion.ITEMS_STATE
import ru.tensor.sbis.marks.style.SbisMarksStyleHolder
import ru.tensor.sbis.marks.utils.ShadowView
import ru.tensor.sbis.marks.view.SbisMarksListView
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment

/**
 * Фрагмент в котором происходит отображение списка пометок.
 * Передача выбранных пользователем данных реализована с помощью FragmentResultApi
 *
 * @property items          список моделей пометок
 * @property componentType  тип списка пометок
 *
 * @author ra.geraskin
 */
internal class MarksMovablePanelFragment(
    private var items: List<SbisMarksElement> = emptyList(),
    private var componentType: SbisMarksComponentType = SbisMarksComponentType.COLOR
) : Fragment(), Content {

    private val marksList: SbisMarksListView by lazy {
        SbisMarksListView(
            requireContext(),
            items = items,
            componentType = componentType,
            styleHolder = styleHolder,
            selectionChangeListener = { onSelectionChange() },
            selectionCompleteListener = { items -> onSelectionComplete(items) }
        )
    }

    private val cancelButton: SbisLinkButton by lazy {
        createCancelHeaderButton(
            context = requireContext(),
            isVisibleOnStart = marksList.getSelected().isNotEmpty(),
            styleHolder = styleHolder,
            onCancel = {
                marksList.clearAll()
                onSelectionChange()
                if (componentType != SbisMarksComponentType.WITH_ADDITIONAL_MARKS) onSelectionComplete()
            }
        )
    }

    private val headerView: View by lazy {
        createHeaderView(
            context = requireContext(),
            cancelButton = cancelButton,
            isCheckboxVisible = componentType.isCheckboxVisible,
            onAccept = { onSelectionComplete() }
        )
    }

    private val shadowView: ShadowView by lazy { ShadowView(requireContext()) }

    private val scrollView: NestedScrollView by lazy {
        NestedScrollView(requireContext()).apply {
            addView(marksList, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            setOnScrollChangeListener(shadowView.scrollListener)
        }
    }

    private val scrollViewContainer: FrameLayout by lazy {
        FrameLayout(requireContext()).apply {
            addView(scrollView)
            addView(shadowView)
        }
    }

    private val rootListLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            orientation = VERTICAL
            addView(headerView)
            addView(scrollViewContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    private val styleHolder: SbisMarksStyleHolder by lazy {
        SbisMarksStyleHolder.create(requireContext(), componentType.isCheckboxVisible)
    }

    /**
     * Использование lazy необходимо, чтобы для каждого экземпляра фрагмента высота панели считалась единожды, с учётом
     * максимальной доступной высоты. В противном случае при каждом новом onLayout (свернуть-развернуть приложение),
     * будет происходить перерасчёт высоты панельки на основе уже изменённой высоты, что приводило к не корректной
     * высоте шторки.
     */
    private val measuredViewHeight: Int by lazy { measureViewHeight() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) return
        if (items.isNotEmpty()) return

        items = savedInstanceState.getParcelableArrayListUniversally(ITEMS_STATE, SbisMarksElement::class.java)
            ?: emptyList()

        componentType = savedInstanceState
            .getParcelableUniversally(COMPONENT_TYPE_STATE, SbisMarksComponentType::class.java)
            ?: SbisMarksComponentType.COLOR_STYLE

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        rootListLayout

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.doOnLayout {
            rootListLayout.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, measuredViewHeight)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(ITEMS_STATE, ArrayList(items))
        outState.putParcelable(COMPONENT_TYPE_STATE, componentType)
    }

    private fun onSelectionComplete(list: List<SbisMarksElement> = marksList.getSelected()) {
        // Нужно для передачи результата выбора пользователя сразу в прикладной фрагмент,
        // чтобы не выполнять транзит в ContainerMovableFragment
        check(parentFragment is ContainerMovableFragment) {
            "Parent fragment is not ContainerMovableFragment instance."
        }
        activity?.supportFragmentManager?.setFragmentResult(FRAGMENT_RESULT_KEY, createResultBundle(list))
        if (parentFragment is Container.Closeable) {
            (parentFragment as Container.Closeable).closeContainer()
        }
    }

    private fun onSelectionChange() {
        cancelButton.isVisible = marksList.getSelected().isNotEmpty()
    }

    override fun onBackPressed(): Boolean = true

    private fun createResultBundle(list: List<SbisMarksElement>) = Bundle().apply {
        putParcelableArrayList(ITEMS_STATE, ArrayList(list))
        putParcelable(COMPONENT_TYPE_STATE, componentType)
    }

    private fun measureViewHeight(): Int {
        val availableHeight = getAvailableHeight().toFloat()
        val panelHeight = (requireView().parent as View).measuredHeight.toFloat()
        val panelGripFieldHeight = panelHeight - requireView().measuredHeight.toFloat()
        val ratio = panelHeight / availableHeight
        val viewHeight = (MAX_HEIGHT_RATIO * availableHeight) - panelGripFieldHeight
        return if (ratio > MAX_HEIGHT_RATIO) {
            viewHeight.toInt()
        } else {
            LayoutParams.MATCH_PARENT
        }
    }

    private fun getAvailableHeight(): Int {
        val screenHeight = requireActivity().window.decorView.height
        var navigationBarHeight = 0
        ViewCompat.getRootWindowInsets(requireView())?.getInsets(WindowInsetsCompat.Type.navigationBars())?.let {
            navigationBarHeight = it.bottom + it.top
        }
        return screenHeight - navigationBarHeight
    }

    companion object {
        /** Соотношение (2/3 = 0.666) максимальной возможной высоты шторки к доступному пространству. */
        private const val MAX_HEIGHT_RATIO = 0.666
    }

}