@file:Suppress("DEPRECATION")

package ru.saby_clients.pagination

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.fragment.HideKeyboardOnScrollListener
import ru.tensor.sbis.common.util.findLastVisibleItemPosition
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.DefaultDividerItemDecoration
import ru.tensor.sbis.viper.ui.SabyClientsListView
import ru.tensor.sbis.design.R as RDesign

/**
 * Абстрактная реализация фрагмента для односторонней пагинации
 */
abstract class SabyClientsPaginationFragment<
    DM : Any,
    ADAPTER : SabyClientsPaginationAdapter<DM>,
    VIEW : SabyClientsPaginationContract.View<DM>,
    PRESENTER : SabyClientsPaginationContract.Presenter<VIEW>> :
    BasePresenterFragment<VIEW, PRESENTER>(),
    SabyClientsPaginationContract.View<DM> {

    /**
     * Ссылка на объект адаптера
     */
    protected lateinit var adapter: ADAPTER

    /**
     * Ссылка на объект компонента списка
     */
    abstract val listView: SabyClientsListView

    /**
     * Цвет для фона за списком
     */
    open val viewBackgroundColor: Int
        get() = requireContext().getColorFromAttr(RDesign.attr.unaccentedAdaptiveBackgroundColor)

    /**
     * Цвет для фона с заглушкой
     */
    open val stubBackgroundColor: Int
        get() = viewBackgroundColor

    /**
     * Ссылка на ресурс цвета для фона заглушки
     */
    @ColorRes
    open val stubViewBacgroundColor: Int = RDesign.color.palette_color_transparent

    /**
     * Декторатор между элементами списка
     */
    protected val listItemDecoration by lazy { DefaultDividerItemDecoration(requireContext()) }

    private var itemDecorationEnabled = false

    // Установить itemDecoration, отключено по умолчанию
    protected fun setItemDecorationEnabled(enabled: Boolean) {
        itemDecorationEnabled = enabled
    }

    private var viewJustCreated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        createRootView(inflater, container).apply {
            initListView()
            initViews()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewJustCreated = true
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Метод создания отображения
     */
    abstract fun createRootView(inflater: LayoutInflater, container: ViewGroup?): View

    /**
     * Инициализация компонентов отображения
     */
    open fun initViews() = Unit

    /**
     * Инициализация комопнента списка
     */
    @CallSuper
    open fun initListView() {
        listView.apply {
            setLayoutManager(LinearLayoutManager(requireContext()))
            setAdapter(adapter)
            if (itemDecorationEnabled) addItemDecoration(listItemDecoration)
            setInProgress(true)
            setOnRefreshListener { presenter.onRefresh() }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (viewJustCreated) viewJustCreated = false
                    else presenter.onScroll(
                        dy,
                        recyclerView.findLastVisibleItemPosition(),
                        recyclerView.computeVerticalScrollOffset()
                    )
                }
            })
            addOnScrollListener(HideKeyboardOnScrollListener())
            recyclerView.clipToPadding = false
            recyclerView.isNestedScrollingEnabled = true
            recyclerView.fitsSystemWindows = false
            //Нужно для скролла при пустом списке
            setRecyclerViewVisibilityStatus(View.VISIBLE)
            setStubBackgroundColor(getColorFrom(stubViewBacgroundColor))
        }
    }

    override fun onStop() {
        stopScroll()
        super.onStop()
    }

    override fun onDestroyView() {
        listView.setAdapter(null)
        listView.clearOnScrollListener()
        super.onDestroyView()
    }

    /**
     * Принудительно остановиться скролл списка
     */
    protected fun stopScroll() {
        listView.recyclerView.stopScroll()
    }

    override fun updateDataList(dataList: ArrayList<DM>) {
        adapter.setContent(dataList)
        resetStubBackground()
    }

    override fun addContentToDataList(dataList: ArrayList<DM>) {
        adapter.addContent(dataList)
        resetStubBackground()
    }

    override fun showMainLoadingProgress(showLoadingProgress: Boolean) {
        if (showLoadingProgress) {
            adapter.setContent(emptyList())
            resetStubBackground()
        }
        if (showLoadingProgress.not()) listView.isRefreshing = false
        listView.setInProgress(showLoadingProgress)
        listView.updateViewState()
    }

    override fun showStubView(msgResId: Int) {
        listView.showInformationViewData(createEmptyViewContent(msgResId))
        setViewBackgroundColor(stubBackgroundColor)
    }

    override fun showListLoadingProgress(showLoadingProgress: Boolean) {
        adapter.showLoadingProgress = showLoadingProgress
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyDataSetChanged() = adapter.notifyDataSetChanged()

    override fun updateItem(position: Int, item: DM) = adapter.updateItem(position, item)

    /**
     * Метод конфигурации заглушки
     */
    open fun createEmptyViewContent(@StringRes msgResId: Int): StubViewContent =
        SabyClientsListView.getStubContent(requireContext(), titleResId = msgResId)

    open fun resetStubBackground() {
        setViewBackgroundColor(viewBackgroundColor)
    }

    private fun setViewBackgroundColor(@ColorInt color: Int) {
        view?.apply { setBackgroundColor(color) }
    }
}