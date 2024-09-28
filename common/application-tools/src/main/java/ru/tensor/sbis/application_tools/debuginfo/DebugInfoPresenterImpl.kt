package ru.tensor.sbis.application_tools.debuginfo

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.application_tools.BuildConfig
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.base.BasePresenter
import ru.tensor.sbis.application_tools.debuginfo.model.DebugInfo

/**
 * @author du.bykov
 *
 * @SelfDocumented */
class DebugInfoPresenterImpl(private val networkNameProvider: NetworkNameProvider) :
    BasePresenter<DebugInfoSettingsFragment> {
    private val data = ArrayList<DebugInfo>()
    private val disposables = CompositeDisposable()
    private var view: DebugInfoSettingsFragment? = null

    override fun attachView(view: DebugInfoSettingsFragment) {
        this.view = view
        if (data.isEmpty()) {
            prepareDebugInfo()
        }
        showDebugInfoData()
    }

    override fun detachView() {
        view = null
    }

    override fun onDestroy() {
        data.clear()
        disposables.clear()
    }

    private val deviceInfo = """
            Manufacturer: ${Build.MANUFACTURER}
            Brand: ${Build.BRAND}
            Model: ${Build.MODEL}
            Product: ${Build.PRODUCT}
            Android: ${Build.VERSION.RELEASE}
            SDK: ${Build.VERSION.SDK_INT}
            Incr.: ${Build.VERSION.INCREMENTAL}
            """.trimIndent()

    private val networkInfo: String
        get() {
            val networkInfo = StringBuilder()
            val currentNetwork = networkNameProvider.getNetworkName()
            networkInfo.append("Current network: ").append(currentNetwork)
            return networkInfo.toString()
        }

    private val screenInfo: String
        get() = view?.run {
            val displayMetrics = resources.displayMetrics
            val configuration = resources.configuration
            """
                DDpi: $densityDpi
                WidthDP: ${configuration.screenWidthDp}
                HeightDP: ${configuration.screenHeightDp}
                Density: ${displayMetrics.density}
                XDPI: ${displayMetrics.xdpi}
                YDPI: ${displayMetrics.ydpi}
                Screen layout size: ${getScreenLayoutTitle(configuration)}
                Resolution (HxW): ${getResolutionTitle(requireActivity())}
                """.trimIndent()
        } ?: ""

    private val buildConfigInfo = """
            Application ID: ${BuildConfig.APPLICATION_ID}
            Build type: ${BuildConfig.BUILD_TYPE}
            Is debug? ${BuildConfig.DEBUG}
            Is Public Build? ${BuildConfig.PUBLIC_BUILD}
            Version code: ${BuildConfig.VERSION_CODE}
            Version name: ${BuildConfig.VERSION_NAME}
            """.trimIndent()

    private fun prepareDebugInfo() {
        data.clear()
        data.add(DebugInfo(R.string.application_tools_device_information, deviceInfo))
        data.add(DebugInfo(R.string.application_tools_screen_information, screenInfo))
        data.add(DebugInfo(R.string.application_tools_build_config, buildConfigInfo))
        data.add(DebugInfo(R.string.application_tools_network_information, networkInfo))
    }

    private fun showDebugInfoData() {
        if (view != null) {
            view!!.showData(data)
        }
    }

    private fun getScreenLayoutTitle(configuration: Configuration) =
        when (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> "XLarge"
            else -> "Undefined"
        }

    private fun getResolutionTitle(activity: Activity) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = activity.windowManager.currentWindowMetrics.bounds
        "${bounds.height()} x ${bounds.width()}"
    } else {
        val bounds = Rect()
        activity.window.decorView.getDrawingRect(bounds)
        "${bounds.height()} x ${bounds.width()}"
    }
}