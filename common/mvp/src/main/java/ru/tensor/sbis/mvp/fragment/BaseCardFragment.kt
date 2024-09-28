package ru.tensor.sbis.mvp.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.mvp.presenter.loadcontent.BaseCardView

private const val LAYOUT_MANAGER_STATE = "BaseCardFragment.LAYOUT_MANAGER_STATE"

/**
 * Базовый фрагмент карточки. Предполагает наличие в макете [Toolbar] и [AbstractListView].
 * @param VIEW  - тип представления
 * @param PRESENTER     - тип презентера
 * @param VIEW_MODEL    - тип вью-модели
 * @param EMPTY_DATA    - тип данных для empty view
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class BaseCardFragment<VIEW, PRESENTER, VIEW_MODEL, EMPTY_DATA>
    : BasePresenterFragment<VIEW, PRESENTER>(), BaseCardView<VIEW_MODEL, EMPTY_DATA>
    where PRESENTER : BasePresenter<VIEW> {

    protected var toolbar: Toolbar? = null
        private set

    protected var listView: AbstractListView<out View, EMPTY_DATA>? = null
        private set

    protected abstract val toolbarId: Int

    protected abstract val listViewId: Int

    protected abstract fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createView(inflater, container, savedInstanceState)
        toolbar = view?.findViewById(toolbarId)
        listView = view?.findViewById(listViewId)
        toolbar?.let { initToolbar(it) }
        listView?.let { initListView(it) }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar = null
        listView?.setAdapter(null)
        listView = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        listView?.let {
            val layoutManagerState = it.recyclerView.layoutManager?.onSaveInstanceState()
            outState.putParcelable(LAYOUT_MANAGER_STATE, layoutManagerState)
        }
    }

    protected open fun restoreFromBundle(savedInstanceState: Bundle) {
        val retainedLayoutManagerState: Parcelable = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE)!!
        listView?.let {
            it.recyclerView.layoutManager?.onRestoreInstanceState(retainedLayoutManagerState)
        }
    }

    /**
     * Иниализирировать тулбар. Переопределите этот метод, если
     * необходима кастомизация внешнего вида тулбара.
     */
    protected open fun initToolbar(toolbar: Toolbar) {
        toolbar.leftPanel.visibility = View.VISIBLE
        toolbar.leftPanel.setOnClickListener { activity?.finish() }
    }

    /**
     * Инициализация списка. Переопределите этот метод, если
     * необходима кастомизация списка.
     */
    protected open fun initListView(listView: AbstractListView<out View, EMPTY_DATA>) {
        listView.setLayoutManager(createLayoutManager())
    }

    /** @SelfDocumented */
    protected open fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    /**
     * Обновить заголовок тулбара.
     */
    protected open fun updateToolbarTitle(title: CharSequence?) {
        toolbar?.leftText?.text = title
    }

    /**
     * Обновить данные для empty view.
     */
    override fun updateEmptyView(data: EMPTY_DATA?) {
        listView?.showInformationViewData(data)
    }

}