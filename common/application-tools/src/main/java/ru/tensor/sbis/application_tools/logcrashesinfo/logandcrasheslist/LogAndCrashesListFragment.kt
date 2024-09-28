package ru.tensor.sbis.application_tools.logcrashesinfo.logandcrasheslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.application_tools.DebugInfoAdapter
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.base.BasePresenterFragment
import ru.tensor.sbis.application_tools.debuginfo.DebugClickListener
import ru.tensor.sbis.application_tools.debuginfo.model.BaseDebugInfo
import ru.tensor.sbis.application_tools.debuginfo.model.DebugInfo
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.CrashInfoFragment
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView

/**
 * @author du.bykov
 *
 * Экран отображения данных о логах и крашах.
 */
class LogAndCrashesListFragment :
    BasePresenterFragment<LogAndCrashesListFragment, LogAndCrashesListPresenter>(),
    DebugClickListener {
    private var mAdapter: DebugInfoAdapter? = null

    @StringRes
    private var mKey = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(
            R.layout.application_tools_debug_info_settings_fragment,
            container,
            false
        )
        if (arguments != null) {
            mKey = requireArguments().getInt(FRAGMENT_KEY_BUNDLE)
        }
        initToolbar(mainView)
        initViews(mainView)
        return mainView
    }

    private fun initToolbar(rootView: View) {
        rootView.findViewById<SbisTopNavigationView>(R.id.sbisToolbar)?.run {
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Res(mKey)
            )
            backBtn?.setOnClickListener { requireActivity().onBackPressed() }
        }
    }

    private fun initViews(mainView: View) {
        val mDebugInfoRecyclerView = mainView.findViewById<RecyclerView>(R.id.debug_info_list)
        val layoutManager = LinearLayoutManager(context)
        mDebugInfoRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.settings_debug_info_divider
            )!!
        )
        mDebugInfoRecyclerView.addItemDecoration(dividerItemDecoration)
        mAdapter = DebugInfoAdapter()
        mAdapter!!.setDebugClickListener(this)
        mDebugInfoRecyclerView.adapter = mAdapter
    }

    fun showData(dataList: ArrayList<DebugInfo>, isTypeEqualsCrash: Boolean) {
        if (dataList.isEmpty()) {
            showToast(isTypeEqualsCrash)
        }
        mAdapter!!.setData(dataList)
    }

    private fun showToast(isTypeEqualsCrash: Boolean) {
        val msg =
            if (isTypeEqualsCrash) getString(R.string.application_tools_crashes_not_exist) else getString(R.string.application_tools_logs_not_exist)
        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showCrashInfoFragment(crashPosition: Int) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.container_frame,
                CrashInfoFragment.newInstance(crashPosition),
                LogAndCrashesListFragment::class.java.canonicalName
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDebugInfoClick(type: BaseDebugInfo.Type, crashPosition: Int) {
        when (type) {
            BaseDebugInfo.Type.CRASH -> showCrashInfoFragment(crashPosition)
            else -> {}
        }
    }

    override fun inject() {
        //ignore
    }

    override fun createPresenter(): LogAndCrashesListPresenter {
        return LogAndCrashesListPresenter()
    }

    //region BaseFragment
    override fun getPresenterView() = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.init(mKey)
    } //endregion

    companion object {
        private const val FRAGMENT_KEY_BUNDLE = "FRAGMENT_KEY_BUNDLE"
        fun newInstance(@StringRes key: Int): LogAndCrashesListFragment {
            val fragment = LogAndCrashesListFragment()
            val args = Bundle()
            args.putInt(FRAGMENT_KEY_BUNDLE, key)
            fragment.arguments = args
            return fragment
        }
    }
}