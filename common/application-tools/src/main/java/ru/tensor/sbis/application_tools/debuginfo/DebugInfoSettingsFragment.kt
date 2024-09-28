package ru.tensor.sbis.application_tools.debuginfo

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.application_tools.DebugInfoAdapter
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.base.BasePresenterFragment
import ru.tensor.sbis.application_tools.debuginfo.model.DebugInfo
import ru.tensor.sbis.common.util.addNavigationArg
import ru.tensor.sbis.common.util.doIfNavigationDisabled
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.insets.addTopPaddingByInsets

/**
 * @author du.bykov
 *
 * @SelfDocumented */
class DebugInfoSettingsFragment : BasePresenterFragment<DebugInfoSettingsFragment, DebugInfoPresenterImpl>() {
    private var mAdapter: DebugInfoAdapter? = null
    private val drawable: Drawable?
        get() = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.settings_debug_info_divider
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = inflater.inflate(
            R.layout.application_tools_debug_info_settings_fragment,
            container,
            false
        )
        initToolbar(mainView)
        initViews(mainView)
        return mainView
    }

    private fun initToolbar(rootView: View) {
        rootView.findViewById<SbisTopNavigationView>(R.id.sbisToolbar).run {
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Value(
                    requireArguments().getString(FRAGMENT_TITLE_BUNDLE, "")
                )
            )
            backBtn?.setOnClickListener { requireActivity().onBackPressed() }
            doIfNavigationDisabled(this@DebugInfoSettingsFragment) { showBackButton = false }
            addTopPaddingByInsets(this)
        }
    }

    private fun initViews(mainView: View) {
        val mDebugInfoRecyclerView = mainView.findViewById<RecyclerView>(R.id.debug_info_list)
        val layoutManager = LinearLayoutManager(context)
        mDebugInfoRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            layoutManager.orientation
        )
        dividerItemDecoration
            .setDrawable(drawable!!)
        mDebugInfoRecyclerView.addItemDecoration(dividerItemDecoration)
        mAdapter = DebugInfoAdapter()
        mDebugInfoRecyclerView.adapter = mAdapter
    }

    val densityDpi: Int
        get() {
            val metrics = DisplayMetrics()
            requireActivity()
                .windowManager
                .defaultDisplay
                .getMetrics(metrics)
            return metrics.densityDpi
        }

    fun showData(dataList: ArrayList<DebugInfo>) {
        mAdapter!!.setData(dataList)
    }

    override fun inject() {
        //ignore
    }

    override fun createPresenter() = DebugInfoPresenterImpl(NetworkNameProvider(requireContext().applicationContext))

    //region BaseFragment
    override fun getPresenterView() = this

    companion object {
        private const val FRAGMENT_TITLE_BUNDLE = "FRAGMENT_TITLE_BUNDLE"

        @JvmOverloads
        fun newInstance(title: String, withNavigation: Boolean? = true): DebugInfoSettingsFragment {
            val fragment = DebugInfoSettingsFragment()
            val args = Bundle()
            args.putString(FRAGMENT_TITLE_BUNDLE, title)
            addNavigationArg(args, withNavigation!!)
            fragment.arguments = args
            return fragment
        }
    }
}