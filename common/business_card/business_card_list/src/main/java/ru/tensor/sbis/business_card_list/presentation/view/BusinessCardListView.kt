package ru.tensor.sbis.business_card_list.presentation.view

import android.view.View
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.base_components.fragment.HideKeyboardOnScrollListener
import ru.tensor.sbis.business_card_list.R
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListStore.Intent
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListStore.State
import ru.tensor.sbis.business_card_list.databinding.BusinessCardListFragmentBinding
import ru.tensor.sbis.business_card_list.di.view.BusinessCardListViewModel
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType
import ru.tensor.sbis.design.utils.insets.Position
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.OneSideDividerDrawerBefore
import ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.OneSideDividerItemDecoration
import ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.createDecorationDrawable
import javax.inject.Inject
import ru.tensor.sbis.design.R as RDesign

/** Представления UI экрана реестра визиток */
internal class BusinessCardListView(
    root: View,
    inject: (BusinessCardListView) -> Unit
) : BaseMviView<State, Intent>(),
    DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    /**@SelfDocumented */
    @Inject
    lateinit var businessCardListViewModel: BusinessCardListViewModel

    private val binding = BusinessCardListFragmentBinding.bind(root)
    private val listView = binding.businessCardListView

    init {
        inject(this)
        initView()
        initInsets()
    }

    override val renderer: ViewRenderer<State> = diff {
        diff(State::needToInitList) { if (it) initList() }
    }

    private fun initView() {
        binding.businessCardListToolbar.apply {
            content = SbisTopNavigationContent.LargeTitle(
                PlatformSbisString.Res(R.string.business_card_list_toolbar_title)
            )
            showBackButton = true
            backBtn?.setOnClickListener { dispatch(Intent.OnToolbarBackClicked) }
        }
    }


    private fun initList() {
        listView.apply {
            with(list) {
                floatingPanelPadding(has = true)
                isVerticalScrollBarEnabled = true
                addOnScrollListener(HideKeyboardOnScrollListener())
                addItemDecoration(
                    OneSideDividerItemDecoration(
                        context,
                        OneSideDividerDrawerBefore.Side.BOTTOM,
                        createDecorationDrawable(
                            dp(8), getColorFromAttr(RDesign.attr.unaccentedAdaptiveBackgroundColor)
                        )
                    )
                )
            }
            bindViewModel(businessCardListViewModel)
        }
    }

    private fun initInsets() {
        initInsetListener(
            DefaultViewInsetDelegateParams(
                listOf(ViewToAddInset(binding.businessCardListToolbar, listOf(IndentType.PADDING to Position.TOP)))
            )
        )
    }
}