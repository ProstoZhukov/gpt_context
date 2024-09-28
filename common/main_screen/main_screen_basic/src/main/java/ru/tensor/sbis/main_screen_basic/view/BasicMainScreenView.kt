package ru.tensor.sbis.main_screen_basic.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.extentions.ViewMargins
import ru.tensor.sbis.design.utils.extentions.applyHeight
import ru.tensor.sbis.design.utils.extentions.doOnApplyWindowInsets
import ru.tensor.sbis.design.utils.insets.addTopPaddingByInsets
import ru.tensor.sbis.main_screen_basic.BasicMainScreenPlugin
import ru.tensor.sbis.main_screen_basic.R
import ru.tensor.sbis.main_screen_basic.permission.setupPermissionHandler
import ru.tensor.sbis.main_screen_basic.statusbar.DefaultStatusBarBackgroundPanelBehavior
import ru.tensor.sbis.main_screen_basic.widget.BasicMainScreenWidget
import ru.tensor.sbis.main_screen_common.util.manageBy
import ru.tensor.sbis.main_screen_decl.basic.BasicContentController
import ru.tensor.sbis.main_screen_decl.basic.BasicMainScreenViewApi
import ru.tensor.sbis.main_screen_decl.basic.TopNavigationConfigurator
import ru.tensor.sbis.main_screen_decl.basic.data.FragmentContainer
import ru.tensor.sbis.main_screen_decl.basic.data.OverlayStatusBarBackgroundPanelBehavior
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenId
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Компонент "Раскладка" (навигация без МП и аккордеона).
 *
 * @see [setupPermissionHandler]
 *
 * @author us.bessonov
 */
class BasicMainScreenView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BasicMainScreenViewApi {

    private val topNavigation = SbisTopNavigationView(context)
        .also { addView(it, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) }

    private val mainContainer = FragmentContainerView(context)
        .also {
            it.id = R.id.basic_main_screen_inner_container_id
            addView(it, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

    private var isStatusBarTransparent = false

    private lateinit var statusBarBackgroundPanel: View

    private var overlayStatusBarBackgroundPanelBehavior: OverlayStatusBarBackgroundPanelBehavior =
        DefaultStatusBarBackgroundPanelBehavior()

    private val topNavigationConfigurator by lazy {
        TopNavigationConfiguratorImpl(
            topNavigation,
            mainScreenWidget,
            BasicMainScreenPlugin.countersSubscriptionProvider?.get(),
            mainScreenWidget.host.viewLifecycleOwner,
            fragmentManager
        )
    }

    private lateinit var mainScreenWidget: BasicMainScreenWidget
    private lateinit var fragmentManager: FragmentManager

    override val host: MainScreenHost
        get() = mainScreenWidget.host

    init {
        if (id == View.NO_ID) id = R.id.basic_main_screen_view_id
        configureInsets()
    }

    override fun setup(
        host: MainScreenHost,
        fragmentManager: FragmentManager,
        contentController: BasicContentController,
        contentScreenId: ScreenId,
        intentHandleExtensions: List<IntentHandleExtension<out IntentHandleExtension.ExtensionKey>>,
        @IdRes
        customTopContainerId: Int?,
        monitorPermissionsOnLifecycle: Boolean
    ) {
        mainScreenWidget = BasicMainScreenWidget(
            host,
            this,
            FragmentContainer(customTopContainerId ?: id, fragmentManager),
            mainContainer.id,
            BasicMainScreenPlugin.permissionFeature?.get()?.permissionChecker,
            host.fragmentActivity.intent,
            intentHandleExtensions,
            monitorPermissionsOnLifecycle
        ).apply {
            defaultContentController = contentController
            defaultScreenId = contentScreenId
        }
        this.fragmentManager = fragmentManager
        configureStatusBarBackgroundPanel(fragmentManager)
    }

    override fun activate() = with(mainScreenWidget) {
        manageBy(host.viewLifecycleOwner)
    }

    override fun configureTopNavigation(
        configure: TopNavigationConfigurator.() -> Unit
    ) = with(topNavigationConfigurator) {
        configure()
        updateTopNavigationViews()
    }

    override fun enableTransparentStatusBar(
        overlayStatusBarBackgroundPanelBehavior: OverlayStatusBarBackgroundPanelBehavior?
    ) {
        isStatusBarTransparent = true
        overlayStatusBarBackgroundPanelBehavior?.let {
            this.overlayStatusBarBackgroundPanelBehavior = it
        }
        ensureStatusBarTransparent()
        statusBarBackgroundPanel = addStatusBarBackgroundPanel()
    }

    override fun onNewIntent(intent: Intent) = mainScreenWidget.handleNewIntent(intent)

    override fun findDisplayedScreen(id: ScreenId) = mainScreenWidget.findDisplayedScreen(id)

    override fun monitorPermissionScope(permissionScope: PermissionScope): LiveData<PermissionLevel?> =
        mainScreenWidget.monitorPermissionScope(permissionScope)

    override fun <K : IntentHandleExtension.ExtensionKey, E : IntentHandleExtension<K>> getIntentHandleExtension(key: K): E? =
        mainScreenWidget.getIntentHandleExtension(key)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
        topNavigation.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST))
        measureChildWithMargins(mainContainer, widthMeasureSpec, 0, heightMeasureSpec, topNavigation.measuredHeight)
        applyToOverlayViews {
            measureChildWithMargins(this, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        setMeasuredDimension(availableWidth, availableHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        topNavigation.layout(left, top)
        mainContainer.layout(left + mainContainer.paddingLeft, topNavigation.bottom)
        applyToOverlayViews {
            layoutChild(this, left, top, right, bottom)
        }
    }

    private fun layoutChild(view: View, parentLeft: Int, parentTop: Int, parentRight: Int, parentBottom: Int) {
        val lp = view.layoutParams as LayoutParams
        val width = view.measuredWidth
        val height = view.measuredHeight
        val childTop: Int

        val gravity = lp.gravity.takeUnless { it == -1 }
            ?: (Gravity.TOP or Gravity.START)
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK

        val childLeft = when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.CENTER_HORIZONTAL -> parentLeft + (parentRight - parentLeft - width) / 2 +
                lp.leftMargin - lp.rightMargin

            Gravity.RIGHT -> parentRight - width - lp.rightMargin

            Gravity.LEFT -> parentLeft + lp.leftMargin

            else -> parentLeft + lp.leftMargin
        }
        childTop = when (verticalGravity) {
            Gravity.TOP -> parentTop + lp.topMargin

            Gravity.CENTER_VERTICAL -> parentTop + (parentBottom - parentTop - height) / 2 +
                lp.topMargin - lp.bottomMargin

            Gravity.BOTTOM -> parentBottom - height - lp.bottomMargin

            else -> parentTop + lp.topMargin
        }
        view.layout(childLeft, childTop, childLeft + width, childTop + height)
    }

    private fun applyToOverlayViews(action: View.() -> Unit) {
        children.filterNot { it == topNavigation || it == mainContainer }
            .forEach { it.action() }
    }

    private fun configureInsets() {
        addTopPaddingByInsets(topNavigation)
    }

    private fun ensureStatusBarTransparent() = with(host.fragmentActivity.window) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT
    }

    private fun addStatusBarBackgroundPanel() = View(context).apply {
        setBackgroundColor(BackgroundColor.HEADER.getValue(context))
        isVisible = false
        val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, Gravity.TOP)
        addView(this, 1, lp)
        doOnApplyWindowInsets { view: View, insets: WindowInsets, _: ViewMargins ->
            val height = insets.systemWindowInsetTop
            if (view.layoutParams.height != height) {
                view.applyHeight(height)
            }
        }
    }

    private fun configureStatusBarBackgroundPanel(fragmentManager: FragmentManager) {
        fun Fragment.isMainContent() = id == mainContainer.id

        fun FragmentManager.hasMainContent() = fragments.any { it.isMainContent() }

        host.fragmentActivity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentViewDestroyed(fm, f)
                    if (isStatusBarTransparent && fm.fragments.size <= 1) {
                        ensureStatusBarTransparent()
                    }
                }
            }, false
        )
        fragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentViewDestroyed(fm, f)
                if (isStatusBarTransparent) {
                    overlayStatusBarBackgroundPanelBehavior.onFragmentViewRemoved(
                        statusBarBackgroundPanel,
                        f,
                        fm,
                        fm.hasMainContent()
                    )
                }
            }

            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                if (isStatusBarTransparent) {
                    if (!f.isMainContent()) v.fitsSystemWindows = true
                    overlayStatusBarBackgroundPanelBehavior.onFragmentViewAdded(
                        statusBarBackgroundPanel,
                        f,
                        fm,
                        fm.hasMainContent()
                    )
                }
            }
        }, false)
    }

}