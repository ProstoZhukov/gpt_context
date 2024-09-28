package ru.tensor.sbis.modalwindows.optionscontent

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.modalwindows.bottomsheet.AbstractBottomOptionsSheet
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOptionsAdapter
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.common.R as RCommon

/**
 * Реализация списка опций для отображения внутри контейнера.
 * Решает проблему переиспользования опций при необходимости отображения внутри различных контейнеров
 * (например, использовать разные контейнеры в зависимости от конфигурации устройства)
 * Является альтернативой [AbstractBottomOptionsSheet], абстрагированной от способа показа.
 *
 * @author sr.golovkin
 */
abstract class AbstractOptionSheetContentFragment<
        VIEW: AbstractOptionSheetContentContract.View,
        PRESENTER: AbstractOptionSheetContentContract.Presenter<VIEW, OPTION>,
        OPTION: BottomSheetOption>
    : BasePresenterFragment<VIEW, PRESENTER>(), AbstractOptionSheetContentContract.View, Content {


    protected var adapter: BottomSheetOptionsAdapter<OPTION>? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themedInflater = inflater.cloneInContext(ContextThemeWrapper(requireActivity(), getThemeRes()))
        val view = createView(themedInflater, container, savedInstanceState)
        val optionsListView = getOptionsRecyclerView(view)
        if (optionsListView != null) {
            val isLandscape = resources.getBoolean(RCommon.bool.is_landscape)
            val options = presenter.createOptions(isLandscape)
            val listener = BottomSheetOptionsAdapter.Listener<OPTION>{ option, _, _ -> presenter.onOptionClick(option) }
            adapter = createOptionsAdapter(options, isLandscape, listener)
            configureRecyclerView(optionsListView, adapter!!)
        }
        return view
    }

    /**
     * Получить [RecyclerView], отображающее список опций
     */
    open fun getOptionsRecyclerView(root: View): RecyclerView? {
        return root.findViewById(R.id.modalwindows_options_list)
    }

    /**
     * Создать экземпляр [RecyclerView.LayoutManager]
     */
    open fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    /**
     * Сконфигурировать [RecyclerView]
     */
    open fun configureRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = createLayoutManager()
        // Workaround to remove "blinking" effect when notifyItemChanged gets called
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    /**
     * Создать адаптер опций, отображаемых в этом фрагменте
     */
    abstract fun createOptionsAdapter(
            options: List<OPTION>,
            isLandscape: Boolean,
            listener: BottomSheetOptionsAdapter.Listener<OPTION>
    ): BottomSheetOptionsAdapter<OPTION>

    /**
     * Создать View контента
     */
    protected open fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(getLayoutRes(), container, false)
    }

    @LayoutRes
    protected open fun getLayoutRes() = R.layout.modalwindows_default_options_bottom_sheet

    //region boilerplate/not implemented
    override fun inject() {
        //not implemented
    }

    override fun onCloseContent() {
        //not implemented
    }
    //endregion

    @StyleRes
    private fun getThemeRes(): Int {
        return requireContext().getDataFromAttrOrNull(R.attr.optionSheetTheme, true)
                ?: R.style.ModalWindowsOptionSheetTheme
    }
}