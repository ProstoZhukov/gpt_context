package ru.tensor.sbis.design.link_share.presentation.view

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arkivanov.mvikotlin.core.view.BaseMviView
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.link_share.R
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareStore.Intent
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareStore.State
import ru.tensor.sbis.design.link_share.databinding.LinkShareFragmentBinding
import ru.tensor.sbis.design.link_share.presentation.adapter.LinkShareAdapter
import ru.tensor.sbis.design.link_share.utils.CopyLinkMenuItemStrategy
import ru.tensor.sbis.design.link_share.utils.OpenInBrowserMenuItemStrategy
import ru.tensor.sbis.design.link_share.utils.OpenQRMenuItemStrategy
import ru.tensor.sbis.design.link_share.utils.SendLinkMenuItemStrategy
import ru.tensor.sbis.design.tabs.api.SbisTabsStyle
import ru.tensor.sbis.design.tabs.util.tabs
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.COPY
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.CUSTOM
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.OPEN_IN_BROWSER
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.QR
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.SEND
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItemModel
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMode
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams
import ru.tensor.sbis.design.R as RDesign

/**
 * Представления UI экрана "поделиться ссылкой"
 * */
internal class LinkShareView(
    private val binding: LinkShareFragmentBinding,
    private val shareAdapter: LinkShareAdapter,
    private val params: SbisLinkShareParams,
    private val fm: () -> FragmentManager?
) : BaseMviView<State, Intent>() {

    private val resources
        get() = binding.root.resources

    init {
        initLinkTitleView()
        initListView()
        initTabsView()
        initClicks()
    }

    private fun initLinkTitleView() {
        with(binding.sbisLinkShareTitle) {
            text = params.title.ifEmpty { resources.getString(R.string.link_share_title_link) }
        }
        with(binding.sbisLinkShareLeftIcon) {
            isVisible = params.mode == SbisLinkShareMode.ON_MOVABLE_PANEL_WITH_BACK_BUTTON
            setTextColor(context.getColorFromAttr(RDesign.attr.toolbarBackIconColor))
            setOnClickListener { fm()?.popBackStack() }
        }
    }

    private fun initListView() {
        val menuItems = mutableListOf<BaseItem<Any>>().apply {
            // Всегда показываем три дефолтных таба в начале и QR в конце списка.
            addAll(listOf(COPY, OPEN_IN_BROWSER, SEND).map { it.toMenuItem().toBaseItem() })

            params.customMenuItem.forEach { customItem ->
                if (customItem.group.isNotEmpty()) {
                    add(BaseItem(data = customItem.group, type = R.id.sbis_link_share_custom_block_title))
                }
                customItem.menuItems.forEach { menuItem ->
                    add(SbisLinkShareMenuItemModel(CUSTOM, menuItem.icon, menuItem.title, menuItem.action).toBaseItem())
                }
            }
            if (params.customMenuItem.isNotEmpty()) {
                add(BaseItem(R.id.sbis_link_share_custom_block_line, data = ""))
            }
            add(QR.toMenuItem().toBaseItem())
        }

        with(binding.sbisLinkShareList) {
            layoutManager = LinearLayoutManager(context)
            adapter = shareAdapter
            shareAdapter.setContent(menuItems)
        }
    }

    private fun SbisLinkShareMenuItem.toMenuItem(): SbisLinkShareMenuItemModel {
        val strategies = mapOf(
            COPY to CopyLinkMenuItemStrategy(),
            OPEN_IN_BROWSER to OpenInBrowserMenuItemStrategy(),
            QR to OpenQRMenuItemStrategy(),
            SEND to SendLinkMenuItemStrategy()
        )
        val strategy = strategies[this]

        return strategy?.let {
            SbisLinkShareMenuItemModel(
                this,
                resources.getString(strategy.getIconResId()),
                resources.getString(it.getTextResId())
            )
        } ?: SbisLinkShareMenuItemModel()
    }

    private fun SbisLinkShareMenuItemModel.toBaseItem(): BaseItem<Any> {
        return BaseItem(
            data = this,
            type = R.id.sbis_link_share_base_item
        )
    }

    private fun initTabsView() {
        with(binding.sbisLinkShareTabsView) {
            // Показываем виджет вкладок, если ссылок больше одной.
            visibility = if (params.links.size <= 1) View.GONE else View.VISIBLE
            if (visibility == View.VISIBLE) {
                createTabs()
                setTabClickListener()
            }
        }
    }

    private fun SbisTabsView.createTabs() {
        tabs = tabs {
            params.links.forEach { link ->
                tab {
                    content { text(link.caption) }
                    style = SbisTabsStyle(
                        customSelectedTitleColor = SbisColor.Attr(RDesign.attr.labelContrastTextColor),
                        customUnselectedTitleColor = SbisColor.Attr(RDesign.attr.labelTextColor),
                        customMarkerColor = SbisColor.Attr(RDesign.attr.labelTextColor)
                    )
                }
            }
        }
    }

    private fun SbisTabsView.setTabClickListener() {
        setOnTabClickListener {
            dispatch(Intent.TabLinkSelected(params.links, it.content))
        }
    }

    private fun initClicks() {
        shareAdapter.onItemClick = { dispatch(Intent.MenuItemClicked(it.type)) }
    }
}