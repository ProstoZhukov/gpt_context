package ru.tensor.sbis.our_organisations.presentation.list.ui

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.list_utils.decoration.drawer.divider.SolidDividerDrawer
import ru.tensor.sbis.design.list_utils.decoration.dsl.decorate
import ru.tensor.sbis.design.list_utils.decoration.dsl.viewTypes
import ru.tensor.sbis.design.list_utils.decoration.predicate.position.OffsetPredicate
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.SeparatorColor
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.databinding.OurOrgFragmentListBinding
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListView.Event
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListView.Model
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OurOrgListAdapter
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.PaginationScrollListener
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.TYPE_ORGANISATION_ITEM

/**
 * Обертка над [binding] фрагмента экрана нашей организации.
 *
 * @author mv.ilin
 */
internal class OurOrgListViewImpl(
    private val binding: OurOrgFragmentListBinding,
    private val ourOrgParams: OurOrgParams,
    ourOrgItemType: OurOrgItemType
) : BaseMviView<Model, Event>(), OurOrgListView {

    private var adapter: OurOrgListAdapter

    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        adapter = OurOrgListAdapter(
            ourOrgItemType,
            ourOrgParams.isMultipleChoice,
            { organisationVm ->
                dispatch(Event.OrganisationItemClicked(organisationVm, true))
            },
            { organisationVm ->
                dispatch(Event.OrganisationItemClicked(organisationVm))
            }
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        with(binding) {
            collapsingToolbar.apply {
                val params = layoutParams as AppBarLayout.LayoutParams

                if (needHidePanel())
                    params.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                else
                    params.scrollFlags = SCROLL_FLAG_NO_SCROLL

                layoutParams = params
            }

            filterSearchPanel.searchQueryChangedObservable().subscribe {
                if (it.isBlank() || it.trim().length > 2) {
                    dispatch(Event.SearchTextChanged(it))
                }
            }.add(disposables)

            filterSearchPanel.cancelSearchObservable().subscribe {
                binding.filterSearchPanel.setSearchText("")
                dispatch(Event.SearchTextChanged(""))
            }.add(disposables)

            filterSearchPanel.filterClickObservable().subscribe {
                dispatch(Event.OpenFilter(filterSearchPanel))
            }.add(disposables)

            filterSearchPanel.searchFocusChangeObservable()
                .subscribe { isFocusable ->
                    if (!isFocusable) filterSearchPanel.hideKeyboard()
                }
                .add(disposables)

            filterSearchPanel.searchFieldEditorActionsObservable()
                .filter { it == EditorInfo.IME_ACTION_SEARCH }
                .map { filterSearchPanel.getSearchText() }
                .subscribe {
                    filterSearchPanel.hideKeyboard()
                    if (it.isNotBlank()) {
                        dispatch(Event.SearchTextChanged(it))
                    }
                }
                .add(disposables)

            filterSearchPanel.setHasFilter(ourOrgParams.hasFilter)

            organisations.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@OurOrgListViewImpl.adapter
                setHasFixedSize(false)

                addOnScrollListener(
                    PaginationScrollListener(
                        layoutManager = layoutManager as LinearLayoutManager,
                        pageSize = OurOrgListContract.ITEMS_PAGE,
                        requestPage = { dispatch(Event.RequestPage) }
                    )
                )

                decorate {
                    setDrawer(
                        SolidDividerDrawer(
                            SeparatorColor.DEFAULT.getValue(context),
                            false,
                            BorderThickness.S.getDimenPx(context)
                        )
                    )
                    predicate = OffsetPredicate(0, 2)
                    viewTypes { intArrayOf(TYPE_ORGANISATION_ITEM) }
                }
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    dispatch(Event.Refresh)
                }
            }
        }
    }

    override val renderer: ViewRenderer<Model> =
        diff {
            diff(Model::stubContent, set = { stubContent ->
                if (stubContent != null) {
                    binding.emptyView.setContent(stubContent)
                }
                binding.emptyView.isVisible = stubContent != null
                dispatch(Event.OnShowContent)
            })
            diff(Model::needShowSearch, set = { needShowSearch ->
                binding.filterSearchPanel.isVisible = needShowSearch
                binding.swipeRefresh.isEnabled = needShowSearch
            })
            diff(Model::setSelectedFilter, set = { filter ->
                binding.filterSearchPanel.setSelectedFilters(listOfNotNull(filter?.getString(binding.root.context)))
            })
            diff(Model::swipeRefreshing, set = { isRefresh ->
                binding.swipeRefresh.isRefreshing = isRefresh
            })
            diff(Model::showPageProgress, set = { isShow ->
                adapter.showOlderLoadingProgress(isShow)
            })
            diff(Model::emptyProgress, set = { emptyProgress ->
                if (emptyProgress) {
                    binding.swipeRefresh.isEnabled = false
                    binding.outOrgProgressBar.isVisible = true
                } else {
                    binding.swipeRefresh.isEnabled = true
                    binding.outOrgProgressBar.isVisible = false
                }
                dispatch(Event.OnShowContent)
            })
            diff(Model::data, set = { data ->
                if (data != null) {
                    if (data.updatedAll) {
                        adapter.setContent(data.items)
                        dispatch(Event.OnShowContent)

                        if (data.refreshState)
                            binding.organisations.scrollToPosition(0)
                    } else {
                        adapter.addNewPage(data.items)
                    }
                }
            })
        }

    private fun needHidePanel() = ourOrgParams.needHideOnScrollSearchPanel

    override fun onStop() {
        KeyboardUtils.hideKeyboard(binding.root)
    }

    override fun cancel() {
        disposables.clear()
    }

    private fun Disposable.add(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }
}
