package ru.tensor.sbis.application_tools.logcrashesinfo.crashes

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.base.BasePresenterFragment
import ru.tensor.sbis.application_tools.logcrashesinfo.appinfo.AppInfoAdapter
import ru.tensor.sbis.application_tools.logcrashesinfo.appinfo.models.AppInfoViewModel
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.CrashViewModel
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalyticsHelper
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import java.io.File

/**
 * @author du.bykov
 *
 * Экран отображения данных о краше.
 */
class CrashInfoFragment : BasePresenterFragment<CrashInfoFragment, CrashInfoPresenter>() {

    private var mDeviceName: SbisTextView? = null
    private var mDeviceBrand: SbisTextView? = null
    private var mDeviceAndroidVersion: SbisTextView? = null
    private var mCrashLocation: SbisTextView? = null
    private var mCrashReason: SbisTextView? = null
    private var mStackTrace: SbisTextView? = null
    private var mAppInfoDetails: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(
            R.layout.application_tools_crash_description_fragment,
            container,
            false
        )
        initToolbar(mainView)
        initView(mainView)
        if (arguments != null) {
            val crashPosition = requireArguments().getInt(FRAGMENT_KEY_BUNDLE, -1)
            if (crashPosition != -1) {
                presenter.setCrashPosition(crashPosition)
            } else {
                presenter.setCrashName(requireArguments().getString(FRAGMENT_KEY_BUNDLE, ""))
            }
        }
        return mainView
    }

    private fun initView(mainView: View) {
        mDeviceName = mainView.findViewById(R.id.device_name)
        mDeviceBrand = mainView.findViewById(R.id.device_brand)
        mDeviceAndroidVersion = mainView.findViewById(R.id.device_android_version)
        mCrashLocation = mainView.findViewById(R.id.crash_location)
        mCrashReason = mainView.findViewById(R.id.crash_reason)
        mStackTrace = mainView.findViewById(R.id.stacktrace)
        mAppInfoDetails = mainView.findViewById(R.id.app_info_details)
    }

    private fun initToolbar(mainView: View) {
        val sbisToolbar = mainView.findViewById<SbisTopNavigationView>(R.id.sbisToolbar)
        sbisToolbar.content = SbisTopNavigationContent.SmallTitle(
            title = PlatformSbisString.Res(R.string.application_tools_crash_info_title)
        )
        if (activity != null) {
            sbisToolbar.backBtn?.setOnClickListener { requireActivity().onBackPressed() }
        }

        val iconColor = if (sbisToolbar.isOldToolbarDesign) IconColor.CONTRAST else IconColor.DEFAULT
        val iconStyle = SbisButtonIconStyle(
            ColorStateList.valueOf(iconColor.getValue(sbisToolbar.context))
        )
        sbisToolbar.rightItems = listOf(
            SbisLinkButton(requireContext()).apply {
                model = SbisButtonModel(
                    icon = SbisButtonTextIcon(
                        SbisMobileIcon.Icon.smi_share,
                        style = iconStyle
                    )
                )
                size = SbisButtonSize.S
                setOnClickListener { presenter.onShareButtonClicked() }
            }
        )
    }

    fun setCrashInfo(crashViewModel: CrashViewModel) {
        mCrashLocation!!.text = crashViewModel.exactLocationOfCrash
        mCrashReason!!.text = crashViewModel.reasonOfCrash
        mStackTrace!!.text = crashViewModel.stackTrace
        setDeviceInfo(crashViewModel)
        setAppInfo(crashViewModel.appInfoViewModel)
    }

    fun openSendApplicationChooser(crashFile: File) {
        val share = Intent(Intent.ACTION_SEND_MULTIPLE)
        val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.FILE_AUTHORITY, crashFile)
        share.type = "application/txt"
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(listOf(uri)))
        startActivity(
            Intent.createChooser(
                share,
                getString(R.string.application_tools_crash_info_share_dialog_message)
            )
        )
    }

    private fun setAppInfo(viewModel: AppInfoViewModel) {
        mAppInfoDetails!!.adapter = AppInfoAdapter(viewModel)
        mAppInfoDetails!!.layoutManager = LinearLayoutManager(context)
    }

    private fun setDeviceInfo(viewModel: CrashViewModel) {
        mDeviceName!!.text = viewModel.deviceName
        mDeviceBrand!!.text = viewModel.deviceBrand
        mDeviceAndroidVersion!!.text = viewModel.deviceAndroidApiVersion
    }

    override fun inject() {
        //ignore
    }

    override fun createPresenter(): CrashInfoPresenter {
        return CrashInfoPresenter(CrashAnalyticsHelper(requireContext().applicationContext))
    }

    override fun getPresenterView() = this

    companion object {
        fun newInstance(crashFileName: String): CrashInfoFragment {
            val fragment = CrashInfoFragment()
            val args = Bundle()
            args.putString(FRAGMENT_KEY_BUNDLE, crashFileName)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(crashPosition: Int): CrashInfoFragment {
            val fragment = CrashInfoFragment()
            val args = Bundle()
            args.putInt(FRAGMENT_KEY_BUNDLE, crashPosition)
            fragment.arguments = args
            return fragment
        }

        private const val FRAGMENT_KEY_BUNDLE = "FRAGMENT_KEY_BUNDLE"
    }
}