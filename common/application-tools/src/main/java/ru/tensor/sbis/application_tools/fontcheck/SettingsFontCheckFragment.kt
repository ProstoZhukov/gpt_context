package ru.tensor.sbis.application_tools.fontcheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.fontcheck.util.SettingsFontCheckTitleFormatter
import ru.tensor.sbis.application_tools.fontcheck.vm.SettingsFontCheckVM
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingAdapter
import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder
import ru.tensor.sbis.common.util.addNavigationArg
import ru.tensor.sbis.common.util.doIfNavigationDisabled
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.insets.addTopPaddingByInsets
import ru.tensor.sbis.design.R as RDesign

/**
 * @author du.bykov
 *
 * Экран проверки шрифтов.
 */
class SettingsFontCheckFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.application_tools_settings_font_check_fragment, container, false)
        initListView(rootView)
        initToolbar(rootView)
        return rootView
    }

    private fun initListView(rootView: View) {
        val listView = rootView.findViewById<RecyclerView>(R.id.list)
        val adapter =
            object : UniversalBindingAdapter<SettingsFontCheckVM, UniversalViewHolder<SettingsFontCheckVM>>() {
                override fun onCreateViewHolder(parent: ViewGroup, p1: Int): UniversalViewHolder<SettingsFontCheckVM> {
                    return UniversalViewHolder(
                        createBinding(
                            R.layout.application_tools_settings_font_check_list_item,
                            parent
                        )
                    )
                }
            }
        adapter.setContent(createContent())
        listView.let {
            it.adapter = adapter
            it.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            it.addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    context,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun initToolbar(rootView: View) {
        rootView.findViewById<SbisTopNavigationView>(R.id.sbisToolbar).apply {
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Res(R.string.application_tools_debug_font_check_title)
            )
            backBtn?.setOnClickListener { requireActivity().onBackPressed() }
            doIfNavigationDisabled(this@SettingsFontCheckFragment) { showBackButton = false }
            addTopPaddingByInsets(this)
        }
    }

    private fun createContent(): List<SettingsFontCheckVM> {
        val content = mutableListOf<SettingsFontCheckVM>()
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(context, R.string.application_tools_font_check_sizeDisplay0)!!,
                RDesign.dimen.size_display0_scaleOn,
                RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(context, R.string.application_tools_font_check_sizeDisplay1)!!,
                RDesign.dimen.size_display1_scaleOn,
                RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(
                    context,
                    R.string.application_tools_font_check_sizeTitle1,
                )!!, RDesign.dimen.size_title1_scaleOn, RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(
                    context,
                    R.string.application_tools_font_check_sizeTitle2,
                )!!, RDesign.dimen.size_title2_scaleOn, RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(
                    context,
                    R.string.application_tools_font_check_sizeTitle3,
                )!!, RDesign.dimen.size_title3_scaleOn, RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(
                    context,
                    R.string.application_tools_font_check_sizeBody1,
                )!!, RDesign.dimen.size_body1_scaleOn, RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(
                    context,
                    R.string.application_tools_font_check_sizeBody2,
                )!!, RDesign.dimen.size_body2_scaleOn, RDesign.color.text_color_accent_3
            )
        )
        content.add(
            SettingsFontCheckVM(
                SettingsFontCheckTitleFormatter.format(
                    context,
                    R.string.application_tools_font_check_sizeCaption1,
                )!!, RDesign.dimen.size_caption1_scaleOn, RDesign.color.text_color_accent_3
            )
        )
        return content
    }

    companion object {
        @JvmStatic
        fun newInstance(withNavigation: Boolean = true): Fragment =
            addNavigationArg(SettingsFontCheckFragment(), withNavigation)
    }
}